/*
 * The MIT License
 *
 * Copyright 2015 Ken Gichia.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.patrick.delivery.utils.templates;

// Parse the source document to get all the resource tags

import org.springframework.context.MessageSource;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.support.RequestContextUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.InputStream;
import java.util.*;

// Allow one to send a json response
// The internationalisation items



/**
 * This class will be used by the controllers to render a given view using the
 * layout defined.
 *
 * @author Ken Gichia, Anthony Mwawughanga
 * @version 1.0.0
 * @category Template
 * @package Teke
 * @since 2015-12-29
 */
public class View {

    /**
     * Properties managed by this class
     */
    private ModelAndView _view;  // The Data to place in the view
    private MessageSource _mS;   // The item used to fetch the language params
    private Locale _locale;      // The current locale used in displaying content

    /**
     * When one just wants to use the view to generate json responses but at the
     * same time use the language pack
     */
    public View() {
    }

    /**
     * This class constructor initialises the view with the name of the view to
     * use to render the page
     *
     * @param viewName
     */
    public View(String viewName) {
        init(viewName, null);
    }

    /**
     * This class constructor initialises the view with the name of the view to
     * use to render the page and the parameters that should be included in the
     * view to render
     *
     * @param viewName
     * @param map
     */
    public View(String viewName, Map<String, ?> map) {
        init(viewName, map);
    }

    /**
     * Add one attribute at a time to the view
     *
     * @param label
     * @param object
     * @return View
     */
    public View addAttribute(String label, Object object) {
        _view.addObject(label, object);
        return this;
    }

    /**
     * Add/modify attributes used by the view
     *
     * @param map
     * @return View
     */
    public View mergeAttributes(Map<String, ?> map) {
        _view.addAllObjects(map);
        return this;
    }

    /**
     * Use the data managed by this class to get the ModelAndView object that
     * will be used to render a given page
     *
     * @return ModelAndView
     */
    public ModelAndView getView() {
        // Return the view
        return _view;
    }

    /**
     * Call this method to redirect to a given url
     *
     * @param url
     * @return ModelAndView
     */
    public ModelAndView redirect(String url) {
        return new ModelAndView("redirect:/" + url);
    }

    /**
     * This method is used to return a json object from a hashmap
     *
     * @param obj
     * @return ModelAndView
     */
    public ModelAndView sendJSON(Object obj) {
        ModelAndView view = new ModelAndView(new JsonView());
        view.addObject(obj);

        // Get the result
        return view;
    }

    /**
     * When one has to send an access denied JSON response for an ajax request
     * one does not have access to
     *
     * @return ModelAndView
     */
    public ModelAndView accessDeniedJSON() {
        Map<String, Object> map = new HashMap<>();
        map.put("msg", getMessage("ajax.accessDenied", null));
        map.put("status", false);

        return sendJSON(map);
    }

    /**
     * When an auto-complete event could not yield a result
     *
     * @param queryString
     * @return List<Object>
     */
    public List<Object> emptyAutocomplete(String queryString) {
        List<Object> result = new ArrayList<>();
        Map<String, String> row = new HashMap<>();
        String[] params = {"<em>" + queryString + "</em>"};

        row.put("id", "_q");
        row.put("value", getMessage("ajax.emptyAutocomplete", params));

        result.add(row);

        return result;
    }

    /**
     * Get the message to display using the locale that the user has specified
     *
     * @param message
     * @return string
     */
    public String getMessage(String message) {
        return getMessage(message, null);
    }

    /**
     * Get the message to display using the locale that the user has specified
     *
     * @param message
     * @param params
     * @return string
     */
    public String getMessage(String message, Object[] params) {
        // Check if the message source has been initialised
        if (null == _mS)
            _mS = new ClassPathXmlApplicationContext("spring-message-source.xml");

        // Return what has been generated
        return _mS.getMessage(message, params, getLocale());
    }

    /**
     * Called to get the current app locale as used to render the content
     *
     * @return Locale
     */
    public Locale getLocale() {
        // If the locale has already been set, return the current locale
        if (_locale instanceof Locale) return _locale;

        // Get the current locale
        if (null != RequestContextHolder.getRequestAttributes()) {
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
            LocaleResolver localeResolver = RequestContextUtils.getLocaleResolver(request);

            if (null != localeResolver) _locale = localeResolver.resolveLocale(request);
        }

        // Set the default locale if the current locale isn't defined
        if (null == _locale) _locale = new Locale("en", "GB");

        // Return the locale
        return _locale;
    }

    /**
     * This private method initialises the class
     *
     * @param viewName
     */
    private void init(String viewName, Map<String, ?> map) {
        _view = new ModelAndView();

        // Set the content security policy
//        _csp = new CSP();
//        _csp.enableGoogleFonts();

        // Set the view name
//        _view.addObject("_pageTemplate", "views/" + viewName + ".jsp");

        // Parse the html
//        if ( viewName != null && !viewName.isEmpty() )
//            parseTemplate("/WEB-INF/jsp/views/" + viewName + ".jsp");


        // If the view name is set
        if (viewName != null) {
            // Set the view name
            _view.setViewName(viewName);
        }

        // Set the view parameters
        if (map != null) _view.addAllObjects(map);
    }

    /**
     * Parse the jsp file to fetch the html resources to load
     *
     * @param source
     */
    private void parseTemplate(String source) {
        try {
            // Get the serverlet request object
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();

            // Read the template file
            InputStream is = request.getServletContext().getResourceAsStream(source);
            byte[] b = new byte[is.available()];
            is.read(b);

            // Replace the resource tag to make the code below to work
            String text = (new String(b)).replaceAll("<%--", "").replaceAll("--%>", "");

            // Parse the content
//            Document doc = Jsoup.parse(text);
//
//            // Set all css resources
//            setPageCSS(doc.select("cssResource"));
//
//            // Set all javascript resources
//            setPageJS(doc.select("jsResource"));

            // Enable the CSP as per the template
//            setCSP(doc);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Set the CSS as defined in the page template
     *
     * @param   links
     */
//    private void setPageCSS(Elements links) {
//        ArrayList _pageCSS = new ArrayList();
//        for (Element link : links) {
//            for (String item : link.html().split(","))
//                _pageCSS.add(item.trim());
//        }
//
//        if ( _pageCSS.size() > 0 ) _view.addObject("_pageCSS", _pageCSS);
//    }
//
//    /**
//     * Set the JS as defined in the page template
//     *
//     * @param   links
//     */
//    private void setPageJS(Elements links) {
//        ArrayList _pageJS = new ArrayList();
//        String text;
//        for (Element link : links) {
//            for (String item : link.html().split(",")) {
//                text = item.trim();
//
//                if ( "https://maps.googleapis.com/maps/api/js".equals(text) )
//                    text += "?key=" + Params.get("google.mapAPIKey") + "&libraries=places";
//
//                _pageJS.add(text);
//            }
//        }
//
//        if ( _pageJS.size() > 0 ) _view.addObject("_pageJS", _pageJS);
//    }
//
//    /**
//     * Define the content security policy
//     *
//     * @param   doc
//     */
//    private void setCSP(Document doc) {
//        // Define the Image CSP
//        _csp.addImageSources(doc.select("imageCSP"));
//
//        // Define the Font CSP
//        _csp.addFontSources(doc.select("fontCSP"));
//
//        // Define the Style CSP
//        _csp.addStyleSources(doc.select("styleCSP"));
//
//        // Define the Script CSP
//        _csp.addScriptSources(doc.select("scriptCSP"));
//
//        // Set the CSP utilities
//        for (Element link : doc.select("cspUtils")) {
//            for (String item : link.html().split(",")) {
//                System.out.println("CSP Util: "+item);
//
//                switch (item.trim()) {
//                    case "googleFonts":
//                        _csp.enableGoogleFonts();
//                        break;
//                    case "googleMaps":
//                        _csp.enableGoogleMaps();
//                        break;
//                }
//            }
//        }
//
//        // Add the csp list
//        _view.addObject("_pageCSP", _csp.getCSP(Params.get("srv.baseURL")));
//    }


}
