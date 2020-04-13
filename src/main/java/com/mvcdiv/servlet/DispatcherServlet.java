package com.mvcdiv.servlet;

import com.mvcdiv.annotation.*;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebInitParam;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @ProjectName: SpringCode
 * @Package: com.mvcdiv.servlet
 * @ClassName: DispatcherServlet
 * @Author: ZhangJunjie
 * @Description:
 * @Date: 2020/4/13 15:40
 * @Version: 1.0
 */
@WebServlet(name = "dispatcherServlet", urlPatterns = "/*", initParams = @WebInitParam(name = "contextConfigLocation", value = "application.properties"))
public class DispatcherServlet extends HttpServlet {
    /**
     * description:所有配置信息都在此
     * create time: 17:23 2020/4/13
     */
    private Properties properties = new Properties();
    private List<String> classNames = new ArrayList<>();

    /**
     * description:IOC容器
     * create time: 18:14 2020/4/13
     */
    private Map<String, Object> ioc = new HashMap<>();

    private List<Handler> handlerMapping = new ArrayList<>();

    @Override
    public void init(ServletConfig config) throws ServletException {
        //加载配置文件
        doLoadConfig(config.getInitParameter("contextConfigLocation"));
        //扫描所有的相关类
        doScanner(properties.getProperty("scanPackage"));
        //初始化所有的相关类的实例，并且将其放入到IOC容器中
        doInstance();
        //实现依赖注入
        doAutowried();
        //初始化HandlerMapping
        initHandlerMapping();
    }

    private void initHandlerMapping() {
        if (ioc.isEmpty()) {
            return;
        }
        for (Map.Entry<String, Object> entry : ioc.entrySet()) {
            Class<?> clazz = entry.getValue().getClass();

            if (!clazz.isAnnotationPresent(Controller.class)) {
                continue;
            }
            String baseUrl = "";
            if (clazz.isAnnotationPresent(RequestMapping.class)) {
                RequestMapping requestMapping = clazz.getAnnotation(RequestMapping.class);
                baseUrl = requestMapping.value();
            }

            Method[] methods = clazz.getMethods();
            for (Method method : methods) {
                if (!method.isAnnotationPresent(RequestMapping.class)) {
                    continue;
                }
                RequestMapping requestMapping = method.getAnnotation(RequestMapping.class);

                String regex = ("/" + baseUrl + requestMapping.value()).replaceAll("/+", "/");
                Pattern pattern = Pattern.compile(regex);
                handlerMapping.add(new Handler(pattern, entry.getValue(), method));
                System.out.println("mapping " + regex + " , " + method);
            }
        }
    }

    private void doAutowried() {
        if (ioc.isEmpty()) {
            return;
        }
        for (Map.Entry<String, Object> entry : ioc.entrySet()) {
            //获取到所有的字段Field

            //不管是private还是protected还是default都要注入
            Field[] fields = entry.getValue().getClass().getDeclaredFields();
            for (Field field : fields) {
                if (!field.isAnnotationPresent(AutoWried.class)) {
                    continue;
                }
                AutoWried autoWried = field.getAnnotation(AutoWried.class);
                String beanName = autoWried.value().trim();
                if ("".equals(beanName)) {
                    beanName = field.getType().getName();
                }
                //要是访问到私有的或者受保护的，强制授权访问
                field.setAccessible(true);

                try {
                    field.set(entry.getValue(), ioc.get(beanName));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void doInstance() {
        if (classNames.isEmpty()) {
            return;
        }
        //利用反射机制将刚扫描进来的所有ClassNames初始化
        for (String className : classNames) {
            try {
                Class<?> clazz = Class.forName(className);
                //进入bean的实例化阶段
                /**
                 * IOC容器规则：
                 * 1、key默认用类名首字母小写
                 * 2、如果拥有自定义名字，那么优先使用自定义名字
                 * 3、如果是接口，可以巧妙地用接口类型作为key
                 */
                if (clazz.isAnnotationPresent(Controller.class)) {
                    String beanName = clazz.getSimpleName();
                    beanName = lowerFirstWord(beanName);
                    ioc.put(beanName, clazz.getDeclaredConstructor().newInstance());
                } else if (clazz.isAnnotationPresent(Service.class)) {
                    Service service = clazz.getAnnotation(Service.class);
                    Object instance = clazz.getDeclaredConstructor().newInstance();
                    String beanName = service.value().trim();
                    if ("".equals(beanName)) {
                        beanName = lowerFirstWord(beanName);
                        ioc.put(beanName, instance);
                    }
                    Class<?>[] interfaces = clazz.getInterfaces();
                    for (Class<?> inter : interfaces) {
                        //将接口类型作为key
                        ioc.put(inter.getName().trim(), instance);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    private String lowerFirstWord(String name) {
        char[] chars = name.toCharArray();
        chars[0] += 32;
        return String.valueOf(chars);
    }

    private void doScanner(String scanPackage) {
        //进行递归扫描，扫描所有的class
        URL url = this.getClass().getClassLoader().getResource("/" + scanPackage.replaceAll("\\.", "/"));
        File classDir = new File(url.getFile());
        for (File file : classDir.listFiles()) {
            if (file.isDirectory()) {
                doScanner(scanPackage + "." + file.getName());
            } else {
                String className = scanPackage + "." + file.getName().replace(".class", "");
                classNames.add(className);
            }
        }
    }

    private void doLoadConfig(String location) {
        InputStream stream = this.getClass().getClassLoader().getResourceAsStream(location);
        try {
            properties.load(stream);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * description:等待请求
     * create time: 17:05 2020/4/13
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            doDispatch(req, resp);
        } catch (Exception e) {
            resp.getWriter().write("500 Execption,Detials:\r\n" + Arrays.toString(e.getStackTrace()).replaceAll("\\[|\\]", "").replaceAll(",\\s", "\r\n"));
        }
    }

    private void doDispatch(HttpServletRequest req, HttpServletResponse resp) throws IllegalAccessException, IOException, InvocationTargetException {
        try {
            Handler handler = getHander(req);

            if (handler == null) {
                resp.getWriter().write("404 NOT FOUND!");
                return;
            }

            //获取方法的参数列表
            Class<?>[] paramTypes = handler.method.getParameterTypes();
            //保存所有需要自动复制的参数值
            Object[] paramValues = new Object[paramTypes.length];

            Map<String, String[]> params = req.getParameterMap();
            for (Map.Entry<String, String[]> param : params.entrySet()) {
                String values = Arrays.toString(param.getValue()).replaceAll("\\[|\\]", "").replaceAll(",\\s", "");

                //如果找到匹配的对象开始填充
                if (!handler.paramIndexMapping.containsKey(param.getKey())) {
                    continue;
                }
                int index = handler.paramIndexMapping.get(param.getKey());
                paramValues[index] = convert(paramTypes[index], values);
            }

            //设置方法中的request和response对象
            int reqIndex = handler.paramIndexMapping.get(HttpServletRequest.class.getName());
            paramValues[reqIndex] = req;
            int respIndex = handler.paramIndexMapping.get(HttpServletResponse.class.getName());
            paramValues[respIndex] = resp;

            handler.method.invoke(handler.controller, paramValues);
        } catch (Exception e) {
            throw e;
        }
    }

    private Object convert(Class<?> paramType, String values) {
        if (Integer.class == paramType) {
            return Integer.valueOf(values);
        }
        return values;
    }

    private Handler getHander(HttpServletRequest req) {
        if (handlerMapping.isEmpty()) {
            return null;
        }

        String url = req.getRequestURI();
        String contextPath = req.getContextPath();
        url = url.replace(contextPath, "").replaceAll("/+", "/");
        for (Handler handler : handlerMapping) {
            Matcher matcher = handler.pattern.matcher(url);
            //如果没有匹配继续下一个匹配
            if (!matcher.matches()) {
                continue;
            }
            return handler;
        }
        return null;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.doPost(req, resp);
    }

    /**
     * description:Handler记录Controller中的RequestMapping和Method的对应关系
     * create time: 0:29 2020/4/14
     */
    private class Handler {
        //保存方法对应实例
        protected Object controller;
        //保存映射方法
        protected Method method;
        protected Pattern pattern;
        //参数顺序
        protected Map<String, Integer> paramIndexMapping;

        protected Handler(Pattern pattern, Object controller, Method method) {
            this.controller = controller;
            this.pattern = pattern;
            this.method = method;

            paramIndexMapping = new HashMap<>();
            putParamIndexMapping(method);
        }

        private void putParamIndexMapping(Method method) {
            //提取方法中加注解的参数
            Annotation[][] annotations = method.getParameterAnnotations();
            for (int i = 0; i < annotations.length; i++) {
                for (Annotation annotation : annotations[i]) {
                    if (annotation instanceof RequestParam) {
                        String paramName = ((RequestParam) annotation).value().trim();
                        if (!"".equals(paramName)) {
                            paramIndexMapping.put(paramName, i);
                        }
                    }
                }
            }
            //提取方法中的request和response
            Class<?>[] paramsTypes = method.getParameterTypes();
            for (int i = 0; i < paramsTypes.length; i++) {
                Class<?> type = paramsTypes[i];
                if (type == HttpServletRequest.class || type == HttpServletResponse.class) {
                    paramIndexMapping.put(type.getName(), i);
                }
            }
        }
    }
}
