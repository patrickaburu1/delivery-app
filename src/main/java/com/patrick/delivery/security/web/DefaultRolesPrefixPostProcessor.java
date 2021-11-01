package com.patrick.delivery.security.web;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.PriorityOrdered;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;

/**
 * @author patrick on 7/21/20
 * @project sprintel-delivery
 */
public class DefaultRolesPrefixPostProcessor implements BeanPostProcessor, PriorityOrdered {

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName)
            throws BeansException {

        if (bean instanceof DefaultMethodSecurityExpressionHandler) {
            ((DefaultMethodSecurityExpressionHandler) bean).setDefaultRolePrefix(null);
        }
        return bean;
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName)
            throws BeansException {
        return bean;
    }

    @Override
    public int getOrder() {
        return PriorityOrdered.HIGHEST_PRECEDENCE;
    }
}
