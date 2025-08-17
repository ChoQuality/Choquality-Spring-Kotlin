package com.example.choquality.common.config

import nz.net.ultraq.thymeleaf.layoutdialect.LayoutDialect
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.EnableWebMvc
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import org.thymeleaf.spring6.SpringTemplateEngine
import org.thymeleaf.spring6.dialect.SpringStandardDialect
import org.thymeleaf.spring6.templateresolver.SpringResourceTemplateResolver
import org.thymeleaf.spring6.view.ThymeleafViewResolver
import org.thymeleaf.templatemode.TemplateMode
import java.nio.charset.StandardCharsets

@Configuration
@EnableWebMvc
class WebMvcConfig(
    private val applicationContext: ApplicationContext
) : WebMvcConfigurer {
    private val TEMPLATE_RESOLVER_PREFIX = "classpath:/templates/"
    private val TEMPLATE_RESOLVER_SUFFIX = ".html"

    fun templateResolver(): SpringResourceTemplateResolver {
        val templateResolver = SpringResourceTemplateResolver()
        templateResolver.setApplicationContext(this.applicationContext)
        templateResolver.prefix = TEMPLATE_RESOLVER_PREFIX
        templateResolver.suffix = TEMPLATE_RESOLVER_SUFFIX
        templateResolver.templateMode = TemplateMode.HTML
        templateResolver.characterEncoding = StandardCharsets.UTF_8.name()
        templateResolver.isCacheable = false
        return templateResolver
    }

    @Bean
    fun templateEngine(): SpringTemplateEngine {
        val engine = SpringTemplateEngine()
        engine.setTemplateResolver(templateResolver())
        engine.setEnableSpringELCompiler(true)

        val dialect = SpringStandardDialect()
        dialect.enableSpringELCompiler = true
        engine.setDialect(dialect)
        engine.addDialect(LayoutDialect())

        return engine
    }

    @Bean
    fun viewResolver(): ThymeleafViewResolver {
        val resolver = ThymeleafViewResolver()
        resolver.templateEngine = templateEngine()
        resolver.characterEncoding = StandardCharsets.UTF_8.name()
        return resolver
    }

    override fun addResourceHandlers(registry: ResourceHandlerRegistry) {
        registry
            .addResourceHandler(
                "/assets/css/**",
                "/assets/fonts/**",
                "/assets/js/**",
                "/assets/images/**",
                "/assets/vendors/**",
                "/static/js/**",
                "/static/css/**",
                "/static/assets/**"
            )
            .addResourceLocations(
                "classpath:/assets/css/",
                "classpath:/assets/fonts/",
                "classpath:/assets/js/",
                "classpath:/assets/images/",
                "classpath:/assets/vendors/",
                "classpath:/static/js/",
                "classpath:/static/css/",
                "classpath:/static/assets/",
                "classpath:/static/file/"
            )
            .setCachePeriod(31_536_000)
    }
}