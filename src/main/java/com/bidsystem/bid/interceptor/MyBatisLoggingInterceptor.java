package com.bidsystem.bid.interceptor;

import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.text.DateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.regex.Matcher;

/**
 * Mybatis SQL Log 남기기용 Interceptor
 * MappedStatement에서 update, query method를 intercept하여 로그를 남긴다
 */

@Intercepts({
    @Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class}),
    @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class, CacheKey.class, BoundSql.class}),
    @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class})
})


public class MyBatisLoggingInterceptor implements Interceptor {
    private static final Logger logger = LoggerFactory.getLogger(MyBatisLoggingInterceptor.class);

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        MappedStatement mappedStatement = (MappedStatement) invocation.getArgs()[0];
        Object parameter = invocation.getArgs().length > 1 ? invocation.getArgs()[1] : null;
        String sqlId = mappedStatement.getId();

        BoundSql boundSql = mappedStatement.getBoundSql(parameter);
        Configuration configuration = mappedStatement.getConfiguration();
        Object returnValue = null;

        logger.info("\n\n/*---------------Mapper Map ID: {}[begin]---------------*/", sqlId);
        String sql = genSql(configuration, boundSql);
        logger.info("\n\n==> sql:\n {}\n/*{}*/", sql, sqlId);

        long start = System.currentTimeMillis();
        try {
            returnValue = invocation.proceed();
        } catch (InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
        }
        long end = System.currentTimeMillis();
        long time = end - start;
        logger.info("<== sql END {} ms", time);

        return returnValue;
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {
        System.out.println(properties);
    }

    // 정적 메서드들
    private static String getParameterValue(Object obj) {
        if (obj instanceof String) {
            return "'" + obj + "'";
        } else if (obj instanceof Date) {
            DateFormat formatter = DateFormat.getDateTimeInstance(DateFormat.DEFAULT, DateFormat.DEFAULT, Locale.KOREA);
            return "'" + formatter.format((Date) obj) + "'";
        } else {
            return obj != null ? obj.toString() : "";
        }
    }

public static String genSql(Configuration configuration, BoundSql boundSql) {
    Object parameterObject = boundSql.getParameterObject();
    List<?> parameterMappings = boundSql.getParameterMappings();
    String sql = boundSql.getSql();

    if (parameterMappings.size() > 0 && parameterObject != null) {
        if (configuration.getTypeHandlerRegistry().hasTypeHandler(parameterObject.getClass())) {
            // 단일 파라미터 객체에 대한 치환
            sql = sql.replaceFirst("\\?", Matcher.quoteReplacement(getParameterValue(parameterObject)));
        } else {
            // 다중 파라미터 객체에 대한 치환
            org.apache.ibatis.reflection.MetaObject metaObject = configuration.newMetaObject(parameterObject);
            for (Object parameterMapping : parameterMappings) {
                String propertyName = ((org.apache.ibatis.mapping.ParameterMapping) parameterMapping).getProperty();
                Object value;

                if (metaObject.hasGetter(propertyName)) {
                    value = metaObject.getValue(propertyName);
                } else if (boundSql.hasAdditionalParameter(propertyName)) {
                    value = boundSql.getAdditionalParameter(propertyName);
                } else {
                    continue; // 값이 없으면 다음으로 넘어감
                }

                // 안전하게 치환
                sql = sql.replaceFirst("\\?", Matcher.quoteReplacement(getParameterValue(value)));
            }
        }
    }
    return sql;
}

}
