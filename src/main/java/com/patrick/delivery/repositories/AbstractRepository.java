package com.patrick.delivery.repositories;


import com.fasterxml.jackson.databind.JsonNode;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.EntityManager;
import javax.persistence.Id;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.lang.reflect.Field;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.*;


public class AbstractRepository<T> {

    public static final String ACTIVE_RECORD = "1";
    public static final String ACTIVATE_RECORD = "1";
    public static final String INACTIVE_RECORD = "4";
    public static final String DELETE_RECORD = "6";
    public static final String SOFT_DELETED_RECORD = "6";
    public static final int MAX_RECORDS = 1000000;

    private Class<T> _mapping;               // The mapped class
    private static SecureRandom _random;     // The secure random generator
    private String _primaryKey;

    @PersistenceContext(unitName = "delivery")
    private EntityManager entityManager;

    // Initialise the secure random object
    static {
        _random = new SecureRandom();
    }

    /**
     * Get the tool used for randomisation
     *
     * @return SecureRandom
     */
    public static SecureRandom getRandomiser() {
        return _random;
    }

    /**
     * Generate a random string of length n.
     * <p>
     * This works by choosing (n*5) bits from a cryptographically secure random
     * bit generator, and encoding them in base-32.
     * <p>
     * 128 bits is considered to be cryptographically strong. However, this
     * encoding uses 5 random bits per character. Therefore, for a
     * cryptographically strong random string, n >= 26.
     * <p>
     * Compare this to a random UUID, which only has 3.4 bits per character in
     * standard layout, and only 122 random bits in total.
     * <p>
     * Reference:
     * http://stackoverflow.com/questions/41107/how-to-generate-a-random-alpha-numeric-string
     *
     * @param n
     * @return String
     */
    public static String randomString(int n) {
        // Get the default length
        if (n < 4) {
            n = 4;
        }

        // Generate the random string
        return new BigInteger((n * 5), _random).toString(32);
    }

    /**
     * Generate a random string using the default readable characters while
     * adding a few random characters of your choice
     *
     * @param customPad
     * @param n
     * @return String
     */
    public static String randomString(String customPad, int n) {
        // Ensure that the custom pad is not a null
        if (null == customPad) {
            customPad = "";
        }

        String validChars = "ABCDEFGHJKLMNPQRSTUVWXYZabcdefghjklmnpqrstuvwxyz23456789" + customPad;
        StringBuilder pw = new StringBuilder();

        // Get the default length
        if (n < 4) {
            n = 4;
        }

        // Generate the password
        for (int i = 0; i < n; i++) {
            int index = _random.nextInt(validChars.length());
            pw.append(validChars.charAt(index));
        }

        // Return the password
        return pw.toString();
    }

    /**
     * Generate a password string
     *
     * @param n
     * @return String
     */
    public static String securePassword(int n) {
        // Ensure a safe length
        if (n < 8) {
            n = 8;
        }

        // Return the password
        return randomString("!@#$%^&*()-_=+{}[]|:;<>?,./", n);
    }

    /**
     * Generate a random string that will be used for receipt purposes
     *
     * @param n
     * @return String
     */
    public static String receiptNumber(int n) {
        String validChars = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
        StringBuilder pw = new StringBuilder();

        // Get the default length
        if (n < 12) {
            n = 12;
        }

        // Generate the password
        for (int i = 0; i < n; i++) {
            int index = _random.nextInt(validChars.length());
            pw.append(validChars.charAt(index));
        }

        // Return the password
        return pw.toString();
    }

    /**
     * Generate random digits for general purposes
     *
     * @param length
     * @return String
     */
    public static String randomNumber(int length) {
        Random random = new Random();
        char[] digits = new char[length];
        digits[0] = (char) (random.nextInt(9) + '1');
        for (int i = 1; i < length; i++) {
            digits[i] = (char) (random.nextInt(10) + '0');
        }
        return new String(digits);
    }

    /**
     * Get the criteria used by this class and use it to build a query
     *
     * @return Criteria
     */
    public CriteriaQuery<T> createCriteria() {

        //Get Criteria Builder
        CriteriaBuilder builder = getCurrentSession().getCriteriaBuilder();
        //CriteriaQuery<Contact> criteria = builder.createQuery(_mapping);
        return builder.createQuery(_mapping);
//        return getCurrentSession().createCriteria(_mapping);
    }

    /**
     * Get a list of the flags that indicate a record is inactive
     *
     * @return List<String>
     */
    protected List<String> getInactiveFlags() {
        List<String> inactiveList = new ArrayList<String>();

        inactiveList.add(INACTIVE_RECORD);
        inactiveList.add(DELETE_RECORD);
        inactiveList.add(SOFT_DELETED_RECORD);

        return inactiveList;
    }

    /**
     * Get the class that has been mapped to this repository
     *
     * @return Class<T>
     */
    public final Class<T> getMapping() {
        return _mapping;
    }

    /**
     * Set the class that has been mapped to this repository
     *
     * @param mapping
     */
    protected final void setMapping(Class<T> mapping) {
        _mapping = mapping;
    }

    /**
     * Get the current session as identified by this repository
     *
     * @return Session
     */
    protected Session getCurrentSession() {
        return entityManager.unwrap(Session.class);
    }

    /**
     * The name of the primary key column.
     * <p>
     * Override this in the subclasses to define the primary key as desired
     *
     * @return String
     */
    protected String getPrimaryKey() {
        // Ensure that the primary key is defined
        if (null == _primaryKey || _primaryKey.isEmpty()) {
            for (Field fn : getMapping().getDeclaredFields()) {
                if (null != fn.getAnnotation(Id.class)) {
                    _primaryKey = fn.getName();
                    break;
                }
            }
        }

        // Get the primary
        return _primaryKey;
    }
    /**
     * Generate the unique receipt code for the specified column
     *
     * @param column
     * @param length
     * @return String
     */
    public String receiptCode(String column, int length) {
        String code = receiptNumber(length);
        boolean loop = true;
        Iterator it;

        Session session = getCurrentSession();
        while (loop) {
            // Search the db
            it = session
                    .createQuery("from " + this._mapping.getName() + " where " + column + " = :code")
                    .setParameter("code", code)
                    .list().iterator();

            // If the record has not been found, return the code
            if (it.hasNext()) {
                code = receiptNumber(length);
            } else {
                loop = false;
            }
        }

        // Return the code
        return code;
    }

    /**
     * Generate the unique number code using for the specified column Cab be
     * used as a verification token
     *
     * @param column
     * @param length
     * @return String
     */
    public String randomNumberCode(String column, int length) {
        String code = randomNumber(length);
        boolean loop = true;
        Iterator it;

        Session session = getCurrentSession();
        while (loop) {
            // Search the db
            it = session
                    .createQuery("from " + this._mapping.getName() + " where " + column + " = :code")
                    .setParameter("code", code)
                    .list().iterator();

            // If the record has not been found, return the code
            if (it.hasNext()) {
                code = randomNumber(length);
            } else {
                loop = false;
            }
        }

        // Return the code
        return code;
    }

    /**
     * Generate the unique random code using for the specified column
     *
     * @param column
     * @param length
     * @return String
     */
    public String randomCode(String column, int length) {
        String code = randomString("", length);
        boolean loop = true;
        Iterator it;

        Session session = getCurrentSession();
        while (loop) {
            // Search the db
            it = session
                    .createQuery("from " + this._mapping.getName() + " where " + column + " = :code")
                    .setParameter("code", code)
                    .list().iterator();

            // If the record has not been found, return the code
            if (it.hasNext()) {
                code = randomString("", length);
            } else {
                loop = false;
            }
        }

        // Return the code
        return code;
    }

    /**
     * Given a column, generate the unique random code entry for that column
     *
     * @param column
     * @return String
     */
    public String randomCode(String column) {
        String code = new BigInteger(130, _random).toString(32);
        boolean loop = true;
        Iterator it;

        System.err.println("code:" + code);

        Session session = getCurrentSession();
        while (loop) {
            // Search the db
            it = session
                    .createQuery("from " + this._mapping.getName() + " where " + column + " = :code")
                    .setParameter("code", code)
                    .list().iterator();

            // If the record has not been found, return the code
            if (it.hasNext()) {
                code = new BigInteger(130, _random).toString(32);
            } else {
                loop = false;
            }
        }
        // Return the code
        return code;
    }
    public String randomStringGenerator(int length, boolean isPassword) {
        String characters_to_choose_from = "";

        //token string can't include some special characters, so use special characters only for password generation
        if (isPassword)
            characters_to_choose_from = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz!@%*_?#";
        else
            characters_to_choose_from = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

        SecureRandom random = new SecureRandom();

        StringBuilder sb = new StringBuilder(length);

        for (int i = 0; i < length; i++) {
            sb.append(characters_to_choose_from.charAt(random.nextInt(characters_to_choose_from.length())));
        }

        return sb.toString();
    }

    /**
     * Generate the unique random code using the BigInteger class for the
     * specified column
     *
     * @param column
     * @param length
     * @return String
     */
    public String bigIntCode(String column, int length) {
        String code = randomString(length);
        boolean loop = true;
        Iterator it;

        Session session = getCurrentSession();
        while (loop) {
            // Search the db
            it = session
                    .createQuery("from " + this._mapping.getName() + " where " + column + " = :code")
                    .setParameter("code", code)
                    .list().iterator();

            // If the record has not been found, return the code
            if (it.hasNext()) {
                code = randomString(length);
            } else {
                loop = false;
            }
        }

        // Return the code
        return code;
    }

    /**
     * Get the number of records in the table mapped by the entity managed by
     * this class
     *
     * @return Long
     */
    public Long recordCount() {
        return (Long) getCurrentSession()
                .createQuery("SELECT COUNT(" + getPrimaryKey() + ") FROM " + _mapping.getName())
                .list().iterator().next();
    }

    /**
     * Get the iterator which can be used to list the specified records
     * identified by the table in the mapping class
     *
     * @return List<T>
     */
    public List<T> fetchList() {
        return fetchList(0, MAX_RECORDS);
    }

    /**
     * Get the iterator which can be used to list the specified records
     * identified by the table in the mapping class
     *
     * @param offset
     * @param limit
     * @return List<T>
     */
    public List<T> fetchList(int offset, int limit) {
        // Ensure that the proper limit is applied
        if (limit < 1 || limit > MAX_RECORDS) {
            limit = MAX_RECORDS;
        }

        // Ensure a proper offset
        if (offset < 0) {
            offset = 0;
        }

        // Return the iterator
        return getCurrentSession()
                .createQuery("from " + _mapping.getName())
                .setFirstResult(offset)
                .setMaxResults(limit)
                .list();
    }

    /**
     * Get the iterator which can be used to list the specified records
     * identified by the table in the mapping class
     *
     * @param filter
     * @return List<T>
     */
    public List<T> fetchList(Map<String, Object> filter) {
        return fetchList(filter, 0, MAX_RECORDS);
    }

    /**
     * Get the iterator which can be used to list the specified records
     * identified by the table in the mapping class
     *
     * @param filter
     * @param offset
     * @param limit
     * @return List<T>
     */
    public List<T> fetchList(Map<String, Object> filter, int offset, int limit) {
        // Ensure that the proper limit is applied
        if (limit < 1 || limit > MAX_RECORDS) {
            limit = MAX_RECORDS;
        }

        // Ensure a proper offset
        if (offset < 0) {
            offset = 0;
        }
        CriteriaBuilder criteriaBuilder = getCurrentSession().getCriteriaBuilder();
        // When there is something to return, get the criteria
        CriteriaQuery<T> criteria = createCriteria();
        Root<T> root = criteria.from(_mapping);
        final List<Predicate> predicates = new ArrayList<Predicate>();
        // Loop through the map as you set the criteria
        for (Map.Entry<String, ?> p : filter.entrySet()) {
            if (p.getValue().equals("null"))
                predicates.add(criteriaBuilder.isNull(root.get(p.getKey())));
            else if (p.getValue().equals("is not null"))
                predicates.add(criteriaBuilder.isNotNull(root.get(p.getKey())));
            else
                predicates.add(criteriaBuilder.equal(root.get(p.getKey()), p.getValue()));
        }
        criteria.where(criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()])));
        final Query<T> query = getCurrentSession().createQuery(criteria);
        query
                .setFirstResult(offset)
                .setMaxResults(limit);
        return query.list();
    }

    /**
     * Fetch a list of records given their primary keys
     *
     * @param column
     * @param pk
     * @return
     */
    public List<T> fetchList(String column, List<Long> pk) {
        return getCurrentSession()
                .createQuery("FROM " + this._mapping.getName() + " WHERE " + column + " IN (:pkList)")
                .setParameterList("pkList", pk).list();
    }

    /**
     * Fetch a list of records given their values
     *
     * @param column
     * @param values
     * @return
     */
    public List<T> fetchListByValues(String column, List<String> values) {
        return getCurrentSession()
                .createQuery("FROM " + this._mapping.getName() + " WHERE " + column + " IN (:values)")
                .setParameterList("values", values).list();
    }

    /**
     * Search for a given record given the index only
     *
     * @param index
     * @return T
     */
    public T fetchOne(Long index) {
        return (null == index) ? null : getCurrentSession().get(_mapping, index);
    }

    /**
     * Search for a given record given the index only
     *
     * @param index
     * @return T
     */
    public T fetchOne(String index) {
        // When there is nothing to return
        if (null == index || index.isEmpty()) {
            return null;
        }

        // When there is something to return
        try {
            return getCurrentSession().get(_mapping, Long.parseLong(index));
        } catch (NumberFormatException nfe) {
            return null;
        }
    }

    /**
     * Search for a given record given a specified map of records
     *
     * @param params
     * @return T
     */
    public T fetchOne(Map<String, ?> params) {
        // When there is nothing to return
        if (null == params || params.isEmpty()) {
            return null;
        }

        CriteriaBuilder criteriaBuilder = getCurrentSession().getCriteriaBuilder();

        // When there is something to return, get the criteria
        CriteriaQuery<T> criteria = createCriteria();

        Root<T> root = criteria.from(_mapping);

        final List<Predicate> predicates = new ArrayList<>();
        // Loop through the map as you set the criteria
        for (Map.Entry<String, ?> p : params.entrySet()) {
            predicates.add(criteriaBuilder.equal(root.get(p.getKey()), p.getValue()));
        }

        criteria.where(criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()])));

        final Query<T> query = getCurrentSession().createQuery(criteria);


        // Limit the result set
        query.setMaxResults(MAX_RECORDS);

        // Generate the result set
        Iterator<T> it = query.list().iterator();

        // Return the result
        return (it.hasNext()) ? it.next() : null;
    }

    /**
     * Called to insert one entity to the db
     *
     * @param entity
     * @return T
     */
    public T insertRecord(T entity) {
        // Insert the record
        getCurrentSession().persist(entity);

        // Get the inserted record for further processing
        return entity;
    }

    /**
     * Called to Evict one entity from session
     *
     * @param entity
     * @return T
     */
    public T evictRecord(T entity) {
        // Insert the record
        getCurrentSession().evict(entity);

        // Get the inserted record for further processing
        return entity;
    }

    /**
     * When one will have to insert multiple records into the db.
     * <p>
     * Care should be taken to ensure that the number of records passed will not
     * result in memory exhaustion.
     *
     * @param entities
     */
    public void insertRecords(List<T> entities) {
        Session session = getCurrentSession();
        int entitiesSize = entities.size();

        for (int i = 0; i < entitiesSize; i++) {
            session.save(entities.get(i));

            if (i % 200 == 0) {/*Upload batches of 20*/
                //Write changes to the db and release memory:
                session.flush();
            }
        }
    }

    /**
     * Called to update one entity
     *
     * @param entity
     */
    public void saveOrUpdateRecord(T entity) {
        getCurrentSession().saveOrUpdate(entity);
    }

    /**
     * Called to update one entity
     *
     * @param entity
     */
    public void updateRecord(T entity) {
        getCurrentSession().update(entity);
    }

    /**
     * When one will have to update multiple records into the db.
     * <p>
     * Care should be taken to ensure that the number of records passed will not
     * result in memory exhaustion.
     *
     * @param entities
     */
    public void updateRecords(List<T> entities) {
        Session session = getCurrentSession();
        for (T entity : entities) {
            session.update(entity);
        }
    }

    /**
     * When one will have to update multiple records into the db.
     * <p>
     * Care should be taken to ensure that the number of records passed will not
     * result in memory exhaustion.
     *
     * @param entities
     */
    public void updateRecords(Iterator<T> entities) {
        Session session = getCurrentSession();
        while (entities.hasNext()) {
            session.update(entities.next());
        }
    }

    /**
     * Called to specify when a batch of records was updated and by whom
     */
    /**
     * Called to delete one entity
     *
     * @param entity
     */
    public void deleteRecord(T entity) {
        getCurrentSession().delete(entity);
    }

    /**
     * When one will have to delete multiple records from the db.
     * <p>
     * Care should be taken to ensure that the number of records passed will not
     * result in memory exhaustion.
     *
     * @param entities
     */
    public void deleteRecords(List<T> entities) {
        Session session = getCurrentSession();

        for (int i = 0; i < entities.size(); i++) {
            session.delete(entities.get(i));

            if (i % 20 == 0) {/*Upload batches of 20*/
                //Write changes to the db and release memory:
                session.flush();
                session.clear();
            }
        }
    }

    /**
     * When one will have to delete multiple records from the db using a primary
     * key column list passed as a JsonNode object
     *
     * @param column
     * @param keys
     * @return Integer
     */
    public int deleteRecords(String column, JsonNode keys) {
        // Create the parameter list
        Iterator<JsonNode> sds = keys.iterator();
        List<Long> pk = new ArrayList<>();

        while (sds.hasNext()) {
            //sds.
            pk.add(sds.next().asLong());
        }

        // Delete the records
        return getCurrentSession()
                .createQuery("DELETE " + this._mapping.getName() + " WHERE " + column + " IN (:pkList)")
                .setParameterList("pkList", pk)
                .executeUpdate();
    }

}
