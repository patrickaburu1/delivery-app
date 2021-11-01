/*
 * The MIT License
 *
 * Copyright 2016 Ken Gichia.
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
package com.patrick.delivery.utils.interfaces;

/**
 * The interface used by spring to access the class that will be used to perform
 * maker checker tests
 *
 * @category  Security
 * @package   Teke
 * @version   1.0.0
 * @since     2016-01-26
 * @author    Ken Gichia
 */
public interface MakerCheckerInterface {
    /**
     * Check if a given action has been defined for the given user
     *
     * @param   role
     * @return  boolean
     */
    public boolean setDefaultRole(String role);

    /**
     * Call this method to test if the maker checker is active for the given
     * module provided the default role has been defined
     *
     * @param   key
     * @return  boolean
     */
    public boolean isActive(String key);

    /**
     * Call this method to test if the maker checker is active for the given
     * module
     *
     * @param   module
     * @param   key
     * @return  boolean
     */
    public boolean isActive(String module, String key);

    /**
     * Check if a given action has been defined for the given user provided the
     * default role has been defined
     *
     * @param   action
     * @return  boolean
     */
    public boolean hasAction(String action);

    /**
     * Check if a given action has been defined for the given user
     *
     * @param   role
     * @param   action
     * @return  boolean
     */
    public boolean hasAction(String role, String action);
}
