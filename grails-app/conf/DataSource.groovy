dataSource {
    pooled = true
    dbCreate = "update"
    url = "jdbc:h2:mem:devDb;MVCC=TRUE;LOCK_TIMEOUT=10000"
    driverClassName = "org.h2.Driver"
    username = "sa"
    password = ""

    logSql = true
}

hibernate {
    cache.use_second_level_cache = false
    cache.use_query_cache = false
    cache.region.factory_class = 'net.sf.ehcache.hibernate.EhCacheRegionFactory'
}