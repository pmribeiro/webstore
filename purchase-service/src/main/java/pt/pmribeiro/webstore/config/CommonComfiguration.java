package pt.pmribeiro.webstore.config;

import org.springframework.aop.Advisor;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.aop.interceptor.CustomizableTraceInterceptor;
import org.springframework.aop.interceptor.JamonPerformanceMonitorInterceptor;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.context.annotation.Bean;
import org.springframework.web.filter.CommonsRequestLoggingFilter;

import static org.springframework.aop.interceptor.CustomizableTraceInterceptor.*;

/**
 * {@code CommonComfiguration} contains the common configuration
 * with spring beans declaration to catch application metrics
 *
 * Created by pribeiro on 27/11/2016.
 */
public class CommonComfiguration {

    /**
     * Create a CommonsRequestLoggingFilter to log system metrics
     *
     * @return CommonsRequestLoggingFilter
     */
    @Bean
    public CommonsRequestLoggingFilter requestLoggingFilter() {
        CommonsRequestLoggingFilter crlf = new CommonsRequestLoggingFilter();
        crlf.setIncludeClientInfo(true);
        crlf.setIncludeQueryString(true);
        crlf.setIncludePayload(true);
        return crlf;
    }

    /**
     * Create a JamonPerformanceMonitorInterceptor to collect application metrics
     *
     * @return JamonPerformanceMonitorInterceptor
     */
    @Bean
    public JamonPerformanceMonitorInterceptor jamonPerformanceMonitorInterceptor() {
        JamonPerformanceMonitorInterceptor jamonPerformanceMonitorInterceptor = new JamonPerformanceMonitorInterceptor(true, true);
        return jamonPerformanceMonitorInterceptor;
    }

    /**
     * Create Advisor for all methods in pt.pmribeiro.webstore package
     *
     * used by the JamonPerformanceMonitorInterceptor
     * @return Advisor
     */
    @Bean
    public Advisor performanceAdvisor() {
        AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
        pointcut.setExpression("execution(* pt.pmribeiro.webstore..controller..*(..)) || " +
                "execution(* pt.pmribeiro.webstore..service..*(..)) || " +
                "execution(* pt.pmribeiro.webstore..dao..*(..))");
        return new DefaultPointcutAdvisor(pointcut, jamonPerformanceMonitorInterceptor());
    }

    /**
     * Create a CustomizableTraceInterceptor to log events
     *
     * @return CustomizableTraceInterceptor
     */
    @Bean
    public CustomizableTraceInterceptor customizableTraceInterceptor() {
        CustomizableTraceInterceptor cti = new CustomizableTraceInterceptor();
        cti.setEnterMessage("Entering method '" + PLACEHOLDER_METHOD_NAME + "("+ PLACEHOLDER_ARGUMENTS+")' of class [" + PLACEHOLDER_TARGET_CLASS_NAME + "]");
        cti.setExitMessage("Exiting method '" + PLACEHOLDER_METHOD_NAME + "' of class [" + PLACEHOLDER_TARGET_CLASS_NAME + "] took " + PLACEHOLDER_INVOCATION_TIME+"ms.");
        return cti;
    }

    /**
     * Create Advisor for all methods in pt.pmribeiro.webstore package
     * used by the CustomizableTraceInterceptor
     *
     * @return Advisor
     */
    @Bean
    public Advisor traceAdvisor() {
        AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
        pointcut.setExpression("execution(* pt.pmribeiro.webstore..controller..*(..)) || " +
                "execution(* pt.pmribeiro.webstore..service..*(..)) || " +
                "execution(* pt.pmribeiro.webstore..dao..*(..))");
        return new DefaultPointcutAdvisor(pointcut, customizableTraceInterceptor());
    }

}
