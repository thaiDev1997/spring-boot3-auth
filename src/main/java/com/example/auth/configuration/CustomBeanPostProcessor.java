package com.example.auth.configuration;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

@Slf4j
public class CustomBeanPostProcessor implements BeanPostProcessor {

    @Override
    public Object postProcessBeforeInitialization(Object bean,
                                                  String beanName) throws BeansException {
        if (bean instanceof CustomJwtDecoder) {
            log.info(String.format("3) PostProcessor: before %s bean initialization", beanName));
        }
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean,
                                                 String beanName) throws BeansException {
        if (bean instanceof CustomJwtDecoder) {
            log.info(String.format("6) PostProcessor: after %s bean initialization => Bean is available to use", beanName));
        }
        return bean;
    }

}
