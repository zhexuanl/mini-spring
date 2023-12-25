package org.minis;

import org.minis.beans.BeansException;
import org.minis.context.ClassPathXmlApplicationContext;
import org.minis.test.AService;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
      ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("beans.xml");
        AService aService = null;
        try {
            aService = (AService) context.getBean("aService");
        } catch (BeansException e) {
            throw new RuntimeException(e);
        }
        aService.hello();
    }
}
