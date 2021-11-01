package com.patrick.delivery.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorViewResolver;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;


public class AppErrorViewResolver implements ErrorViewResolver {
    private static final Logger logger = LoggerFactory.getLogger(AppErrorViewResolver.class);

    /**
     * Resolve an error view for the specified details.
     *
     * @param request
     * @param httpStatus
     * @param map
     * @return
     */
    @Override
    public ModelAndView resolveErrorView(HttpServletRequest request, HttpStatus httpStatus, Map<String, Object> map) {
        logger.info(httpStatus.getReasonPhrase());
        logger.info(map.toString());
        if (httpStatus == HttpStatus.UNAUTHORIZED)
            return error401(request);
        else if (httpStatus == HttpStatus.FORBIDDEN)
            return error403(request);
        else if (httpStatus == HttpStatus.NOT_FOUND)
            return error404(request);
        else if (httpStatus == HttpStatus.METHOD_NOT_ALLOWED)
            return error405(request);
        else if (httpStatus == HttpStatus.INTERNAL_SERVER_ERROR)
            return error500(request);
        else return error500(request);
    }

    private ModelAndView error403(HttpServletRequest request) {
        ModelAndView view = new ModelAndView("errors/error");

        // Show the error page
        return view
                .addObject("pageTitle", "Access Denied")
                .addObject("pageMessage", "You are not allowed to access the resource specified.")
                .addObject("httpCode", "403");
    }

    private ModelAndView error401(HttpServletRequest request) {
        ModelAndView view = new ModelAndView("errors/error");

        // Show the error page
        return view
                .addObject("pageTitle", "Access Denied")
                .addObject("pageMessage", "You are do not have permission to access the specified resource.")
                .addObject("httpCode", "401")
                ;
    }

    private ModelAndView error405(HttpServletRequest request) {
        ModelAndView view = new ModelAndView("errors/error");

        // Show the error page
        return view
                .addObject("pageTitle", "Page Not Found")
                .addObject("pageMessage", "The HTTP request method is not allowed.")
                .addObject("httpCode", "405")
                ;
    }

    private ModelAndView error404(HttpServletRequest request) {
        ModelAndView view = new ModelAndView("errors/error");

        // Show the error page
        return view
                .addObject("pageTitle", "Page Not Found")
                .addObject("pageMessage", "Sorry, but the page you are looking for has not been found. \nTry checking the URL for error, then hit the refresh button on your browser.")
                .addObject("httpCode", "404")
                ;
    }

    private ModelAndView error500(HttpServletRequest request) {
        ModelAndView view = new ModelAndView("errors/error");

        // Get the error status code
        Integer statusCode = (Integer) request.getAttribute("javax.servlet.error.status_code");

        // Get the error message
        Throwable e = (Throwable) request.getAttribute("javax.servlet.error.exception");

        // Show the error page
        return view
                .addObject("pageTitle", "Oops! Something went wrong.")
                .addObject("pageMessage", "The server encountered something unexpected that did not allow it \n to complete the request. We apologise.")
//                .addObject("httpMsg", HttpStatus.valueOf(statusCode).getReasonPhrase())
                .addObject("httpCode", statusCode)
                .addObject("e", e)
                ;
    }

}
