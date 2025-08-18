package com.example.choquality.common.config

import com.zaxxer.hikari.HikariDataSource
import jakarta.persistence.EntityManagerFactory
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.boot.jdbc.DataSourceBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.env.Environment
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.orm.jpa.JpaTransactionManager
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter
import org.springframework.transaction.PlatformTransactionManager
import java.util.*

@Configuration
@EnableJpaRepositories(
    basePackages = ["com.example.choquality.common.jpa.repo"],
    entityManagerFactoryRef = "entityManagerFactory",
    transactionManagerRef = "transactionManager"
)
@EntityScan(basePackages = ["com.example.choquality.common.jpa.entity"])
class DatasourceConfig(
    private val environment: Environment
) {
    @Bean
    fun dataSource(): HikariDataSource =
        DataSourceBuilder.create()
            .type(HikariDataSource::class.java)
            .driverClassName(environment.getRequiredProperty("choquality.datasource.class"))
            .url(environment.getRequiredProperty("choquality.datasource.url"))
            .build()

    @Bean
    fun entityManagerFactory(dataSource: HikariDataSource): LocalContainerEntityManagerFactoryBean {
        val vendorAdapter = HibernateJpaVendorAdapter().apply {
            setShowSql(true)
            setGenerateDdl(true)
        }

        val jpaProps = Properties().apply {
            put("hibernate.dialect", "org.hibernate.community.dialect.SQLiteDialect")
            put("hibernate.hbm2ddl.auto", "update")
            put("hibernate.format_sql", "true")
        }

        return LocalContainerEntityManagerFactoryBean().apply {
            setPackagesToScan("com.example.choquality.common.jpa.entity")
            setDataSource(dataSource)
            jpaVendorAdapter = vendorAdapter
            setJpaProperties(jpaProps)
        }
    }

    @Bean
    fun transactionManager(emf: EntityManagerFactory): PlatformTransactionManager =
        JpaTransactionManager(emf)
}