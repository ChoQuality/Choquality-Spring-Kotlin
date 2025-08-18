````
당신은 RAG 기반 AI 챗봇으로,
제공된 자바소스를 분석하여 코틀린 소스를 작성해야 합니다.
자바소스를 철저히 검토하고, 코틀린 소스로 명확하게 작성하세요.
소스 내 오류를 고려해 내용을 적절히 보완하세요.
````
````
@Configuration
@EnableWebMvc
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {

    private final ApplicationContext applicationContext;
    private final static String templateResolverPrefix = "classpath:/templates/";
    private final static String templateResolverSuffix = ".html";

    public SpringResourceTemplateResolver templateResolver() {
        SpringResourceTemplateResolver templateResolver = new SpringResourceTemplateResolver();
        templateResolver.setApplicationContext(this.applicationContext);
        templateResolver.setPrefix(templateResolverPrefix);
        templateResolver.setSuffix(templateResolverSuffix);
        templateResolver.setTemplateMode(TemplateMode.HTML);
        templateResolver.setCharacterEncoding(StandardCharsets.UTF_8.name());
        templateResolver.setCacheable(false);
        return templateResolver;
    }
    @Bean
    public SpringTemplateEngine templateEngine() {
        SpringTemplateEngine templateEngine = new SpringTemplateEngine();
        templateEngine.setTemplateResolver(templateResolver());
        templateEngine.setEnableSpringELCompiler(true);

        var dialect = new SpringStandardDialect();
        dialect.setEnableSpringELCompiler(true);
        templateEngine.setDialect(dialect);
        templateEngine.addDialect(new LayoutDialect());
        return templateEngine;
    }

    @Bean
    public ThymeleafViewResolver viewResolver() {
        ThymeleafViewResolver viewResolver = new ThymeleafViewResolver();
        viewResolver.setTemplateEngine(templateEngine());
        viewResolver.setCharacterEncoding(StandardCharsets.UTF_8.name());  // 인코딩 설정 추가
        return viewResolver;
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry
                .addResourceHandler(
                        "/assets/css/**"
                        ,"/assets/fonts/**"
                        ,"/assets/js/**"
                        ,"/assets/images/**"
                        ,"/assets/vendors/**"
                        ,"/static/js/**"
                        ,"/static/css/**"
                        ,"/static/assets/**"
                )
                .addResourceLocations(
                        "classpath:/assets/css/"
                        ,"classpath:/assets/fonts/"
                        ,"classpath:/assets/js/"
                        ,"classpath:/assets/images/"
                        ,"classpath:/assets/vendors/"
                        ,"classpath:/static/js/"
                        ,"classpath:/static/css/"
                        ,"classpath:/static/assets/"
                        ,"classpath:/static/file/"
                )
                .setCachePeriod(31536000);
    }
}

코틀린 소스로 변경
````
````

@Configuration
public class SecurityConfig {


    private final HttpSecurity httpSecurity;
    private final AuthenticationProvider authenticationProvider;
    private final OncePerRequestFilter apiFilter;
    private final OncePerRequestFilter mobileFilter;
    private final OncePerRequestFilter headerFilter;

    public SecurityConfig(HttpSecurity httpSecurity, AuthenticationProvider authenticationProvider, JWTComponent jwtComponent) {
        this.httpSecurity = httpSecurity;
        this.authenticationProvider = authenticationProvider;
        this.apiFilter = new JwtCookieFilter(jwtComponent);
        this.mobileFilter = new BearerFilter(jwtComponent);
        this.headerFilter = new HeaderFilter();
    }


    @Bean
    public SecurityFilterChain getSecurityFilterChain() throws Exception {
        httpSecurity.cors(corsSpec -> corsSpec.configurationSource(corsConfigurationSource()))
                .authenticationProvider(authenticationProvider)
                .csrf(AbstractHttpConfigurer::disable)
                .headers(httpSecurityHeadersConfigurer -> httpSecurityHeadersConfigurer.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable))
                .addFilterBefore(headerFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(apiFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(mobileFilter, UsernamePasswordAuthenticationFilter.class)
                .securityContext((securityContext) ->
                        securityContext
                                .securityContextRepository(new NullSecurityContextRepository())
                )
                .sessionManagement(sessionManagementConfigurer ->
                        sessionManagementConfigurer.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authorizeExchangeSpec -> authorizeExchangeSpec
                        .requestMatchers(HttpMethod.GET,"/assets/css/**","/assets/fonts/**","/assets/vendors/multiupload/**","/assets/js/**","/assets/images/**").permitAll()
                        .requestMatchers(HttpMethod.GET,"/static/js/**","/static/css/**","/static/assets/**").permitAll()
                        .requestMatchers(HttpMethod.GET,"/image/**").permitAll()
                        .requestMatchers(HttpMethod.POST,"/image/**").permitAll()

                        .requestMatchers(HttpMethod.POST
                                ,"/error"
                                ,"/messenger/**"
                                ,"/admin/**"
                                ,"/ai/**"
                                ,"/todo/**"
                                ,"/websocket/**"
                                ,"/portlet/**"
                                ,"/portlet/env/**"
                                ,"/test/**"
                        ).permitAll()
                        .requestMatchers(HttpMethod.GET
                                ,"/admin/**"
                                ,"/ai/**"
                                ,"/messenger/**","/websocket/**"
                                ,"/todo/**","/praise/**", "/report/**"
                                ,"/portlet/**","/portlet/env/**"
                                ,"/test/**"
                        ).permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/interface/**", "/api/**").permitAll()
                        .requestMatchers(HttpMethod.POST,"/api/**").permitAll()
                        .requestMatchers(HttpMethod.GET,"/login/**","/login/logout","/login/keycloak/**").permitAll()
                        .requestMatchers(HttpMethod.POST,"/login/**","/login/mobile","/login/keycloak/**").permitAll()
                        .requestMatchers(HttpMethod.GET,"/","/keycloak").permitAll()
                        .requestMatchers(HttpMethod.OPTIONS).denyAll()
                        .requestMatchers(HttpMethod.GET).denyAll()
                        .requestMatchers(HttpMethod.POST).denyAll()
                        .requestMatchers(HttpMethod.PUT).denyAll()
                        .requestMatchers(HttpMethod.DELETE).denyAll())
        ;
        return httpSecurity.build();
    }

    private CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.addAllowedOriginPattern("*");
        configuration.addAllowedHeader("*");
        configuration.addAllowedMethod("*");
        configuration.setAllowCredentials(true);

        configuration.setMaxAge(3600L);
        var source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
코틀린 내용으로 변경해줘
````
````
class JWTConstant {
    val CookieName: String = "AWP-Cookie"
    val Header_Authorization: String = "Authorization"
    val Patten_Bearer: Pattern = Pattern.compile("^Bearer (.+?)$")
    val Claim_User_Id: String = "User_Id"
    val Claim_User_Name: String = "User_NM"
    val Twelve_hour_second: Long = (3600 * 12).toLong()
}
코틀린은 위와 같이 하면 JWTConstant.CookieNam으로 바로 가능한가? static 없이?
````
````

@Configuration
@RequiredArgsConstructor
public class JWTConfig {

    private final Environment environment;

    @Bean
    public JWTComponent jwtComponent(){
        var jwtComponent = new JWTComponent() {
            private String jwtSecret = "";
            private Algorithm algorithm;
            private JWTVerifier jwtVerifier;

            @Override
            public void initJWT(String key) {
                if(jwtSecret.isEmpty()){
                    this.jwtSecret = environment.getProperty(key);
                    assert this.jwtSecret != null;
                    this.algorithm = Algorithm.HMAC256(this.jwtSecret);
                    this.jwtVerifier = JWT.require(this.algorithm).build();

                } else {
                    throw new SDKException(FAIL_JWT_INIT);
                }
            }
            @Override
            public JWTVerifier getJwtVerifier() {
                return jwtVerifier;
            }

            @Override
            public String createToken(LoginInfoDto loginInfoDto) {
                var now = Instant.now();
                var signed = JWT.create()
                        .withIssuer(JWTConstant.IssuerServer)
                        .withSubject(JWTConstant.Subject)
                        .withClaim(JWTConstant.Claim_Org_Key,loginInfoDto.getOrgKey())
                        .withClaim(JWTConstant.Claim_Org_NM,loginInfoDto.getOrgName())
                        .withClaim(JWTConstant.Claim_Corp_Id,loginInfoDto.getCorpId())
                        .withClaim(JWTConstant.Claim_User_Key,loginInfoDto.getUserKey())
                        .withClaim(JWTConstant.Claim_User_NM,loginInfoDto.getUserName())
                        .withClaim(JWTConstant.Claim_User_IMG,loginInfoDto.getUserImageUrl())
                        .withClaim(JWTConstant.Claim_DB_Key,loginInfoDto.getSelectedDB())
                        .withIssuedAt(now)
                        .withExpiresAt(now.plusSeconds(JWTConstant.Twelve_hour_second))
                        .sign(this.algorithm);
                return signed;
            }
            @Override
            public boolean checkToken(String token) {
                try {
                    var decodedJWT = jwtVerifier.verify(token);
                    return validateToken(decodedJWT);
                } catch (ExpiredJwtException | TokenExpiredException e) {
                    return validateToken(JWT.decode(token));
                } catch (Exception e) {
                    throw new SDKException(FAIL_JWT_VALID);
                }
            }

            @Override
            public LoginInfoDto getLoginInfo(String token) {
                return LoginInfoDto.builder()
                        .orgKey(JWT.decode(token).getClaim(JWTConstant.Claim_Org_Key).asInt())
                        .orgName(JWT.decode(token).getClaim(JWTConstant.Claim_Org_NM).asString())
                        .corpId(JWT.decode(token).getClaim(JWTConstant.Claim_Corp_Id).asInt())
                        .userKey(JWT.decode(token).getClaim(JWTConstant.Claim_User_Key).asInt())
                        .userName(JWT.decode(token).getClaim(JWTConstant.Claim_User_NM).asString())
                        .userImageUrl(JWT.decode(token).getClaim(JWTConstant.Claim_User_IMG).asString())
                        .selectedDB(JWT.decode(token).getClaim(JWTConstant.Claim_DB_Key).asString())
                        .token(token)
                        .build();
            }

            private boolean validateToken(DecodedJWT decodedJWT){
                boolean result;
                result = decodedJWT.getIssuer().equals(JWTConstant.IssuerServer)
                        & decodedJWT.getSubject().equals(JWTConstant.Subject)
                        & decodedJWT.getClaim(JWTConstant.Claim_Org_Key) != null
                        & decodedJWT.getClaim(JWTConstant.Claim_Corp_Id) != null
                        & decodedJWT.getClaim(JWTConstant.Claim_User_Key) != null
                        ;
                return result;
            }
        };
        jwtComponent.initJWT("wiz.jwt.secret");
        return jwtComponent;
    }

}코틀린으로 변경
````
````
@JsonInclude(JsonInclude.Include.NON_NULL)
@Entity
@Table(name = "tb_user_todo")
open class UserTodoEntity {
    @field:JsonIgnore
    @OneToOne(fetch = FetchType.LAZY, cascade = [CascadeType.ALL])
    UserInfoEntity
    TodoInfoEntity
}

아래 테이블의 내용으로 위의 매핑 테이블을 만들어야 해


@JsonInclude(JsonInclude.Include.NON_NULL)
@Entity
@Table(name = "tb_user")
open class UserInfoEntity {
    @EmbeddedId
    open var id: PK? = null

    @Comment("유저 이름")
    @Column(name = "name")
    open var name: String? = null
}

@JsonInclude(JsonInclude.Include.NON_NULL)
@Entity
@Table(name = "tb_todo")
open class TodoInfoEntity {
    @EmbeddedId
    open var id: PK? = null

    @Comment("todo title")
    @Column(name = "title")
    open var title: String? = null

    @Comment("todo writer")
    @Column(name = "writer")
    open var writer: String? = null
}
````
````
@JsonInclude(JsonInclude.Include.NON_NULL)
@Entity
@Table(name = "tb_user")
open class UserInfoEntity {
    @EmbeddedId
    open var id: PK? = null

    @Comment("유저 이름")
    @Column(name = "name")
    open var name: String? = null

    @Comment("유저 이메일")
    @Column(name = "email", unique = true)
    open var email: String? = null

    @Comment("유저 비밀번호")
    @Column(name = "password")
    open var password: String? = null
}
위와 같은 엔터티에 아래같이 선언하니
Cannot find a parameter with this name: id 오류가 나오는데?


 val user = UserInfoEntity(
                    id = PK(1),
                    name = "홍길동",
                    email = "hong@test.com",
                    password = "secret"
                )
````
````
@Configuration
class JWTConfig(
    private val environment: Environment
) {

    @Bean("JWTComponent")
    fun jwtComponent(): JWTComponent {
...

위와 같이 bean으로 등록했는데
아래 class에서는 JWTComponent autowire 할수가 없데 원인이 뭐야
@Configuration
class SecurityConfig(
    private val httpSecurity: HttpSecurity,
    private val authenticationProvider: AuthenticationProvider,
    private val jwtComponent: JWTComponent
) {
````
````
 @Bean
    public HikariDataSource dataSource(){
        return DataSourceBuilder.create()
                .type(HikariDataSource.class)
                .url(environment.getProperty("custom.datasource.url"))
                .username(environment.getProperty("custom.datasource.user"))
                .password(environment.getProperty("custom.datasource.pw"))
                .build();
    }
    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(HikariDataSource dataSource){
        var hibernateJpaVendorAdapter = new HibernateJpaVendorAdapter();
        hibernateJpaVendorAdapter.setShowSql(true);
        hibernateJpaVendorAdapter.setGenerateDdl(true);

        var entityManagerFactoryBean = new LocalContainerEntityManagerFactoryBean();
        entityManagerFactoryBean.setPackagesToScan(ComponentValue.entityPackage);
        entityManagerFactoryBean.setDataSource(dataSource);
        entityManagerFactoryBean.setJpaVendorAdapter(hibernateJpaVendorAdapter);

        return entityManagerFactoryBean;
    }

    @Bean
    public PlatformTransactionManager transactionManager(LocalContainerEntityManagerFactoryBean managerFactoryBean){
        return new JpaTransactionManager(Objects.requireNonNull(managerFactoryBean.getObject()));
    }

위 내용 코틀린으로 변경해주는데 db는 sqllite야
````
````
로그인 로그아웃 부분을 별도의 모듈로 빼고 싶은데 명명을 어떻게 할까?
````
````

public class BearerFilter extends OncePerRequestFilter {

    private final JWTComponent jwtComponent;
    public BearerFilter(JWTComponent jwtComponent) {
        this.jwtComponent = jwtComponent;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        var token = getToken(request);
        token.ifPresent(s -> {
            if(jwtComponent.checkToken(s)){
                var loginInfo = jwtComponent.getLoginInfo(token.get());
                DataSourceContextHolder.setDataSourceKey(loginInfo.getSelectedDB());
                AWPUser awpUser = new AWPUser(loginInfo.getSelectedDB(),"****", Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")),loginInfo);
                var context = SecurityContextHolder.getContext();
                context.setAuthentication(new PreAuthenticatedAuthenticationToken(awpUser,awpUser.getPassword(),awpUser.getAuthorities()));
            }
        });
        try {
            filterChain.doFilter(request, response);
        } finally {
            DataSourceContextHolder.clear();
        }
    }

    private Optional<String> getToken(HttpServletRequest request) {
        return Optional
                .ofNullable(request.getHeader(JWTConstant.Header_Authorization))
                .filter(s -> !s.isEmpty())
                .map(JWTConstant.Patten_Bearer::matcher)
                .filter(Matcher::find)
                .map(matcher -> matcher.group(1));
    }
}

코틀린으로 변환해줘
````
````
@Getter
public class AWPUser extends User {

    private static final long serialVersionUID = 1L;
    private final LoginInfoDto loginInfo;

    public AWPUser(String username, String password, Collection<? extends GrantedAuthority> authorities,LoginInfoDto loginInfo) {
        super(username, password, authorities);
        this.loginInfo = loginInfo;
    }
    public AWPUser(String username, String password, boolean enabled, boolean accountNonExpired, boolean credentialsNonExpired, boolean accountNonLocked, Collection<? extends GrantedAuthority> authorities,LoginInfoDto loginInfo) {
        super(username, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);
        this.loginInfo = loginInfo;
    }
}

코틀린으로 변환
````
````
if (loginInfo != null) {
                val choqualityUser = loginInfo.name?.let {
                    ChoqualityUser(
                        it,
                        "****",
                        setOf(SimpleGrantedAuthority("ROLE_USER")),
                        loginInfo
                    )
                }리펙토링
````
````
스코프 함수 설명해줘
````
````
?.let → null-safe 변환, 체이닝

run → 블록 안에서 가공 후 결과 반환

with → 특정 객체에서 여러 동작 수행

apply → 객체 설정 후 자기 자신 반환 (빌더 느낌)

also → 원본 유지 + 로깅/디버깅 
이건 코틀린만의 방식인거 같아 자바에서는 없는 . 맞아?
````
````
@JsonInclude(JsonInclude.Include.NON_NULL)
@Entity
@Table(name = "tb_user_todo")
open class UserTodoEntity(

    @EmbeddedId
    open var id: UserTodoId? = null,

    @field:JsonIgnore
    @ManyToOne(fetch = LAZY)
    @MapsId("userId")
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    open var user: UserInfoEntity? = null,

    @field:JsonIgnore
    @ManyToOne(fetch = LAZY)
    @MapsId("todoId")
    @JoinColumn(name = "todo_id", referencedColumnName = "id")
    open var todo: TodoInfoEntity? = null
)
에 대한 repository 만들어줘
````
````

    @Bean
    public UserDetailsService userDetailsService() {
        return user -> {
            return new UserDetails() {
                @Override
                public Collection<? extends GrantedAuthority> getAuthorities() {
                    return Collections.singleton(new SimpleGrantedAuthority("ROLE_USER"));
                }

                @Override
                public String getPassword() {
                    return "*****";
                }

                @Override
                public String getUsername() {
                    return user;
                }
            };
        };
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        return new AuthenticationProvider() {
            @Override
            public Authentication authenticate(Authentication authentication) throws AuthenticationException {
                return authentication;
            }

            @Override
            public boolean supports(Class<?> authentication) {
                return true;
            }
        };
    }

    @Bean
    public LoginService loginService(
            AuthenticationProvider authenticationProvider
            , @Qualifier("CorporateId") Map<String, Integer> corporateId
            , JWTComponent jwtComponent
            , PasswordEncoder passwordEncoder
            , ComUserService comUserService
    ) {
        return new LoginService() {
            @Override
            public LoginInfoDto check(String corporate_id, String username) {
                Map<String, String> map = new HashMap<>();
                map.put("corporate_id", corporate_id);
                map.put("user_id", username);
                map.put("use_flag", CommonConstant.USE);
                var loginInfoDto = (LoginInfoDto) sqlSession.selectOne("LoginMapper.attemptLogin", map);
                if(loginInfoDto == null){
                    throw new SDKException(SDKSpec.ERROR_LOGIN_ID);
                }
                return loginInfoDto;
            }

            @Override
            public LoginInfoDto checkUser(String company, String username) {
                Map<String, String> map = new HashMap<>();
                map.put("corporate_id", corporateId.get(company).toString());
                map.put("user_id", username);
                map.put("use_flag", CommonConstant.USE);
                var loginInfoDto = (LoginInfoDto) sqlSession.selectOne("LoginMapper.attemptLogin", map);
                if(loginInfoDto == null){
                    throw new SDKException(SDKSpec.ERROR_LOGIN_ID);
                }
                loginInfoDto.setSelectedDB(company);
                var result = this.attemptMenu(loginInfoDto);
                loginInfoDto.setMenuInfo(result);
                return loginInfoDto;
            }

            @Override
            public Cookie createDefaultCookie() {
                Cookie cookie = new Cookie(CookieName, "");
                cookie.setPath("/");
                cookie.setHttpOnly(true);
                cookie.setSecure(true);
                cookie.setMaxAge(60 * 60 * 24); // 24시간
                return cookie;
            }

            private Authentication createAuthentication(LoginInfoDto loginInfoDto, String password) {
                if (passwordEncoder.matches(password, loginInfoDto.getUserPw())) {
                    return getPreAuthenticatedAuthenticationToken(loginInfoDto);
                } else {
                    return new PreAuthenticatedAuthenticationToken(loginInfoDto, "****");
                }
            }

            private static PreAuthenticatedAuthenticationToken getPreAuthenticatedAuthenticationToken(LoginInfoDto loginInfoDto) {
                loginInfoDto.setUserPw("****");
                SimpleGrantedAuthority simpleGrantedAuthority = new SimpleGrantedAuthority("ROLE_USER");
                List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
                grantedAuthorities.add(simpleGrantedAuthority);
                return new PreAuthenticatedAuthenticationToken(loginInfoDto, "****", grantedAuthorities);
            }

            @Override
            public Cookie attemptLogin(LoginInfoDto loginInfoDto, String password) {
                var cookie = createDefaultCookie();
                if (loginInfoDto == null) {
                    return cookie;
                }
                Authentication authentication = createAuthentication(loginInfoDto, password);
                var result = authenticationProvider.authenticate(authentication);
                if (result.isAuthenticated()) {
                    cookie.setValue(jwtComponent.createToken(loginInfoDto));
                } else {
                    cookie.setValue("");
                }
                return cookie;
            }

            @Override
            public LoginInfoDto attemptMobileLogin(LoginInfoDto loginInfoDto, String password) {

                Authentication authentication = createAuthentication(loginInfoDto, password);
                var result = authenticationProvider.authenticate(authentication);
                if (result.isAuthenticated()) {
                    loginInfoDto.setToken(jwtComponent.createToken(loginInfoDto));
                    return loginInfoDto;
                } else {
                    throw new SDKException(SDKSpec.FAIL_LOGIN);
                }
            }

            @Override
            public List<LoginMenuInfoDto> attemptMenu(LoginInfoDto loginInfoDto) {
                List<LoginMenuInfoDto> loginMenuInfo = sqlSession.selectList("LoginMapper.attemptMenu", loginInfoDto.getUserKey());

                // 일반보고서 메뉴에서 제외처리
                if (!aiInterfaceService.isAiServiceEnabled(AiServiceCd.REPORT_NORMAL)) {
                    loginMenuInfo.removeIf(menu -> "/todo/main/general".equals(menu.getMenuExecutePath()));
                }

                return loginMenuInfo;
            }

            @Override
            public String getMainUrl(List<LoginMenuInfoDto> menuList) {
                return menuList.stream()
                        .filter(loginMenuInfoDto -> loginMenuInfoDto.getMenuLvl() == 0)
                        .map(LoginMenuInfoDto::getMenuExecutePath)
                        .findFirst()
                        .orElse("/");
            }

            @Override
            public void setLoginStatus(TblComUserDto userDto){
                comUserService.updateUserLoginStatus(userDto);
            }


        };
    }

코틀린 으로 변환해줘
````
````
@Embeddable
data class InfoId(
    @Comment("키 id")
    @Column(name = "id")
    var id: Int? = null
) : Serializable


@JsonInclude(JsonInclude.Include.NON_NULL)
@Entity
@Table(name = "tb_todo")
open class TodoInfoEntity (
    @EmbeddedId
    open var id: InfoId? = null,

    @Comment("todo title")
    @Column(name = "title")
    open var title: String? = null,

    @Comment("todo writer")
    @Column(name = "writer")
    open var writer: String? = null
)

@JsonInclude(JsonInclude.Include.NON_NULL)
@Entity
@Table(name = "tb_user")
open class UserInfoEntity (
    @EmbeddedId
    open var id: InfoId? = null,

    @Comment("유저 이름")
    @Column(name = "name")
    open var name: String? = null,

    @Comment("유저 이메일")
    @Column(name = "email", unique = true)
    open var email: String? = null,

    @Comment("유저 비밀번호")
    @Column(name = "password")
    open var password: String? = null

)

@JsonInclude(JsonInclude.Include.NON_NULL)
@Entity
@Table(name = "tb_user_todo")
open class UserTodoEntity(

    @EmbeddedId
    open var id: UserTodoId? = null,

    @field:JsonIgnore
    @ManyToOne(fetch = LAZY)
    @MapsId("userId")
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    open var user: UserInfoEntity? = null,

    @field:JsonIgnore
    @ManyToOne(fetch = LAZY)
    @MapsId("todoId")
    @JoinColumn(name = "todo_id", referencedColumnName = "id")
    open var todo: TodoInfoEntity? = null
)



2025-08-18 09:24:50 | ERROR | org.springframework.boot.SpringApplication | reportFailure | Application run failed 
org.springframework.beans.factory.BeanCreationException: Error creating bean with name 'entityManagerFactory' defined in class path resource [com/example/choquality/common/config/DatasourceConfig.class]: [PersistenceUnit: default] Unable to build Hibernate SessionFactory; nested exception is java.lang.IllegalStateException: PostInitCallback queue could not be processed...
        - PostInitCallbackEntry - EmbeddableMappingType(com.example.choquality.common.jpa.entity.UserTodoEntity#{id})#finishInitialization

	at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.initializeBean(AbstractAutowireCapableBeanFactory.java:1806)
	at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.doCreateBean(AbstractAutowireCapableBeanFactory.java:600)
	at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.createBean(AbstractAutowireCapableBeanFactory.java:522)
	at org.springframework.beans.factory.support.AbstractBeanFactory.lambda$doGetBean$0(AbstractBeanFactory.java:337)
	at org.springframework.beans.factory.support.DefaultSingletonBeanRegistry.getSingleton(DefaultSingletonBeanRegistry.java:234)
	at org.springframework.beans.factory.support.AbstractBeanFactory.doGetBean(AbstractBeanFactory.java:335)
	at org.springframework.beans.factory.support.AbstractBeanFactory.getBean(AbstractBeanFactory.java:205)
	at org.springframework.context.support.AbstractApplicationContext.finishBeanFactoryInitialization(AbstractApplicationContext.java:954)
	at org.springframework.context.support.AbstractApplicationContext.refresh(AbstractApplicationContext.java:625)
	at org.springframework.boot.web.servlet.context.ServletWebServerApplicationContext.refresh(ServletWebServerApplicationContext.java:146)
	at org.springframework.boot.SpringApplication.refresh(SpringApplication.java:754)
	at org.springframework.boot.SpringApplication.refreshContext(SpringApplication.java:456)
	at org.springframework.boot.SpringApplication.run(SpringApplication.java:335)
	at org.springframework.boot.SpringApplication.run(SpringApplication.java:1363)
	at org.springframework.boot.SpringApplication.run(SpringApplication.java:1352)
	at com.example.choquality.proxy.ChoqualityApplicationKt.main(ChoqualityApplication.kt:17)
Caused by: jakarta.persistence.PersistenceException: [PersistenceUnit: default] Unable to build Hibernate SessionFactory; nested exception is java.lang.IllegalStateException: PostInitCallback queue could not be processed...
        - PostInitCallbackEntry - EmbeddableMappingType(com.example.choquality.common.jpa.entity.UserTodoEntity#{id})#finishInitialization

	at org.springframework.orm.jpa.AbstractEntityManagerFactoryBean.buildNativeEntityManagerFactory(AbstractEntityManagerFactoryBean.java:421)
	at org.springframework.orm.jpa.AbstractEntityManagerFactoryBean.afterPropertiesSet(AbstractEntityManagerFactoryBean.java:396)
	at org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean.afterPropertiesSet(LocalContainerEntityManagerFactoryBean.java:366)
	at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.invokeInitMethods(AbstractAutowireCapableBeanFactory.java:1853)
	at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.initializeBean(AbstractAutowireCapableBeanFactory.java:1802)
	... 15 common frames omitted
Caused by: java.lang.IllegalStateException: PostInitCallback queue could not be processed...
        - PostInitCallbackEntry - EmbeddableMappingType(com.example.choquality.common.jpa.entity.UserTodoEntity#{id})#finishInitialization

	at org.hibernate.metamodel.mapping.internal.MappingModelCreationProcess.executePostInitCallbacks(MappingModelCreationProcess.java:144)
	at org.hibernate.metamodel.mapping.internal.MappingModelCreationProcess.execute(MappingModelCreationProcess.java:88)
	at org.hibernate.metamodel.mapping.internal.MappingModelCreationProcess.process(MappingModelCreationProcess.java:42)
	at org.hibernate.metamodel.model.domain.internal.MappingMetamodelImpl.finishInitialization(MappingMetamodelImpl.java:199)
	at org.hibernate.internal.SessionFactoryImpl.initializeMappingModel(SessionFactoryImpl.java:371)
	at org.hibernate.internal.SessionFactoryImpl.<init>(SessionFactoryImpl.java:300)
	at org.hibernate.boot.internal.SessionFactoryBuilderImpl.build(SessionFactoryBuilderImpl.java:457)
	at org.hibernate.jpa.boot.internal.EntityManagerFactoryBuilderImpl.build(EntityManagerFactoryBuilderImpl.java:1506)
	at org.springframework.orm.jpa.vendor.SpringHibernateJpaPersistenceProvider.createContainerEntityManagerFactory(SpringHibernateJpaPersistenceProvider.java:75)
	at org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean.createNativeEntityManagerFactory(LocalContainerEntityManagerFactoryBean.java:390)
	at org.springframework.orm.jpa.AbstractEntityManagerFactoryBean.buildNativeEntityManagerFactory(AbstractEntityManagerFactoryBean.java:409)
	... 19 common frames omitted
	Suppressed: org.hibernate.PropertyNotFoundException: Could not locate field name [id] on class [java.lang.Integer]
		at org.hibernate.internal.util.ReflectHelper.findField(ReflectHelper.java:456)
		at org.hibernate.property.access.internal.PropertyAccessFieldImpl.<init>(PropertyAccessFieldImpl.java:34)
		at org.hibernate.property.access.internal.PropertyAccessStrategyFieldImpl.buildPropertyAccess(PropertyAccessStrategyFieldImpl.java:26)
		at org.hibernate.metamodel.internal.EmbeddableRepresentationStrategyPojo.buildPropertyAccess(EmbeddableRepresentationStrategyPojo.java:162)
		at org.hibernate.metamodel.internal.AbstractEmbeddableRepresentationStrategy.<init>(AbstractEmbeddableRepresentationStrategy.java:44)
		at org.hibernate.metamodel.internal.EmbeddableRepresentationStrategyPojo.<init>(EmbeddableRepresentationStrategyPojo.java:54)
		at org.hibernate.metamodel.internal.ManagedTypeRepresentationResolverStandard.resolveStrategy(ManagedTypeRepresentationResolverStandard.java:156)
		at org.hibernate.metamodel.mapping.internal.EmbeddableMappingTypeImpl.<init>(EmbeddableMappingTypeImpl.java:163)
		at org.hibernate.metamodel.mapping.internal.EmbeddableMappingTypeImpl.from(EmbeddableMappingTypeImpl.java:113)
		at org.hibernate.metamodel.mapping.internal.MappingModelCreationHelper.buildEmbeddedAttributeMapping(MappingModelCreationHelper.java:318)
		at org.hibernate.metamodel.mapping.internal.EmbeddableMappingTypeImpl.finishInitialization(EmbeddableMappingTypeImpl.java:491)
		at org.hibernate.metamodel.mapping.internal.EmbeddableMappingTypeImpl.lambda$from$0(EmbeddableMappingTypeImpl.java:127)
		at org.hibernate.metamodel.mapping.internal.MappingModelCreationProcess$PostInitCallbackEntry.process(MappingModelCreationProcess.java:246)
		at org.hibernate.metamodel.mapping.internal.MappingModelCreationProcess.executePostInitCallbacks(MappingModelCreationProcess.java:106)
		... 29 common frames omitted
Disconnected from the target VM, address: '127.0.0.1:58844', transport: 'socket'

위의 엔터티에 대해 실행하니 아래와 같은 오류가 나와
````
````

@Configuration
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
            setPackagesToScan("com.example.choquality.common.jpa")
            setDataSource(dataSource)
            jpaVendorAdapter = vendorAdapter
            setJpaProperties(jpaProps)
        }
    }

    @Bean
    fun transactionManager(emf: EntityManagerFactory): PlatformTransactionManager =
        JpaTransactionManager(emf)
} 리펙토링 해줘
````
````

@Slf4j
@Controller
@RequestMapping("/login")
@RestController
@RequiredArgsConstructor
public class LoginApiController {

    private final LoginService loginService;
    private final JWTComponent jwtComponent;
    private final KeycloakProperties keycloakProperties;
    private final KeycloakService keycloakService;
    private final Function<KeycloakInfoDto,String> funcGetKeyCloakUrl = keycloakInfo -> keycloakInfo.getKeycloakUrl().concat("/realms/").concat(keycloakInfo.getRealms()).concat("/protocol/openid-connect/token");

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseDto<String>> login(
            HttpServletResponse response
            , @RequestHeader(CommonConstant.companyKey) String companyKey
            , @RequestBody Map<String, String> requestBody) {

        String username = requestBody.get("username");
        String password = requestBody.get("password");
        var loginInfoDto = loginService.checkUser(companyKey, username);
        var mainUrl = loginService.getMainUrl(loginInfoDto.getMenuInfo());
        var cookie = loginService.attemptLogin(loginInfoDto, password);
        response.addCookie(cookie);
        return ResponseEntity.status(HttpStatus.OK)
                .body(ResponseDto.<String>builder()
                        .code(SDKSpec.SUCCESS.getCode())
                        .msg(SDKSpec.SUCCESS.getMessage())
                        .data(mainUrl)
                        .build());
    }

코틀린으로 변환해줘
````
````
@PostMapping(produces = [MediaType.APPLICATION_JSON_VALUE])
    fun login(
        response: HttpServletResponse,
        @RequestBody requestBody: Map<String, String>
    ): ResponseEntity<ResponseDto<String>> {

        val username = requireNotNull(requestBody["username"]) { "username is required" }
        val password = requireNotNull(requestBody["password"]) { "password is required" }

        val loginToken = loginService.attemptLogin(username,password)

        val body = ResponseDto(
            code = SDKSpec.SUCCESS.code,
            msg = SDKSpec.SUCCESS.message,
            data = loginToken
        )
        return ResponseEntity.status(HttpStatus.OK).body(body)
    }
여기서 requireNotNull(requestBody["username"]) null 값이 오면 알아서 뒤의 메세지를 runtime exception으로 주나?
````
````
404 html 작성해줘
````
````
@ControllerAdvice
public class GlobalControllerExceptionHandler {

    @ExceptionHandler({NullPointerException.class,SecurityException.class})
    public ModelAndView handleNullPointerException() {
        ModelAndView model = new ModelAndView();
        model.setViewName("error/403");  // 404 에러 페이지
        return model;
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ModelAndView handleNotFoundException() {
        ModelAndView model = new ModelAndView();
        model.setViewName("error/404");  // 404 에러 페이지
        return model;
    }

    // 기타 모든 예외 처리
    @ExceptionHandler(Exception.class)
    public ModelAndView handleAllExceptions(Exception e) {
        ModelAndView model = new ModelAndView();
        model.setViewName("error/500");  // 404 에러 페이지
        return model;
    }


}

위 내용 코틀린으로
````
````
@GetMapping
    public ModelAndView login(@RequestParam(name = "status",required = false,defaultValue = "default")String status) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // 인증되지 않은 사용자 (익명 사용자 포함) 처리
        if (authentication == null || authentication instanceof AnonymousAuthenticationToken || !authentication.isAuthenticated()) {
            // 인증되지 않은 사용자는 로그인 페이지로 이동
            /*return new ModelAndView("login/testlogin");*/

            var model = new ModelAndView("login/login");
            if(status.equals("loginFail")){
                model.addObject("status",StatusType.ERR_LOGIN);
            } else {
                model.addObject("status",StatusType.DEFAULT);
            }
            return model;
        } else {
            AWPUser user = (AWPUser) authentication.getPrincipal();
            var menu = loginService.attemptMenu(user.getLoginInfo());
            var mainUrl = loginService.getMainUrl(menu);
            return new ModelAndView("redirect:"+ mainUrl);  // 여기를 리다이렉트하고 싶은 페이지로 수정
        }
    }

코틀린으로 변경해줘
````
````

        val result = loginService.saveUser(email,username,password)

        if(result){
            val body = ResponseDto(
                code = SDKSpec.SUCCESS.code,
                msg = SDKSpec.SUCCESS.message,
                data = SDKSpec.SUCCESS.message
            )
            return ResponseEntity.status(HttpStatus.OK).body(body)
        } else {
            val body = ResponseDto(
                code = SDKSpec.FAIL_SAVE_USER.code,
                msg = SDKSpec.FAIL_SAVE_USER.message,
                data = "이메일을 확인해 주세요"
            )
            return ResponseEntity.status(HttpStatus.OK).body(body)
        }
이 부분을 스코프 함수사용으로 변경하는건 어때?
````
````

2025-08-18 13:21:03 | ERROR | org.springframework.boot.SpringApplication | reportFailure | Application run failed 
org.springframework.beans.factory.UnsatisfiedDependencyException: Error creating bean with name 'loginApiController' defined in file [/Users/choquality/Gitlab/private/kotlin/Choquality-Spring-Kotlin/choquality/BuildComponent/common/build/classes/kotlin/main/com/example/choquality/common/controller/api/LoginApiController.class]: Unsatisfied dependency expressed through constructor parameter 0: Error creating bean with name 'loginService' defined in class path resource [com/example/choquality/common/config/AuthConfig.class]: Could not generate CGLIB subclass of class com.example.choquality.common.config.AuthConfig$loginService$1: Common causes of this problem include using a final class or a non-visible class
	at org.springframework.beans.factory.support.ConstructorResolver.createArgumentArray(ConstructorResolver.java:795)
	at org.springframework.beans.factory.support.ConstructorResolver.autowireConstructor(ConstructorResolver.java:237)
	at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.autowireConstructor(AbstractAutowireCapableBeanFactory.java:1375)
	at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.createBeanInstance(AbstractAutowireCapableBeanFactory.java:1212)
	at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.doCreateBean(AbstractAutowireCapableBeanFactory.java:562)
	at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.createBean(AbstractAutowireCapableBeanFactory.java:522)
	at org.springframework.beans.factory.support.AbstractBeanFactory.lambda$doGetBean$0(AbstractBeanFactory.java:337)
	at org.springframework.beans.factory.support.DefaultSingletonBeanRegistry.getSingleton(DefaultSingletonBeanRegistry.java:234)
	at org.springframework.beans.factory.support.AbstractBeanFactory.doGetBean(AbstractBeanFactory.java:335)
	at org.springframework.beans.factory.support.AbstractBeanFactory.getBean(AbstractBeanFactory.java:200)
	at org.springframework.beans.factory.support.DefaultListableBeanFactory.preInstantiateSingletons(DefaultListableBeanFactory.java:975)
	at org.springframework.context.support.AbstractApplicationContext.finishBeanFactoryInitialization(AbstractApplicationContext.java:971)
	at org.springframework.context.support.AbstractApplicationContext.refresh(AbstractApplicationContext.java:625)
	at org.springframework.boot.web.servlet.context.ServletWebServerApplicationContext.refresh(ServletWebServerApplicationContext.java:146)
	at org.springframework.boot.SpringApplication.refresh(SpringApplication.java:754)
	at org.springframework.boot.SpringApplication.refreshContext(SpringApplication.java:456)
	at org.springframework.boot.SpringApplication.run(SpringApplication.java:335)
	at org.springframework.boot.SpringApplication.run(SpringApplication.java:1363)
	at org.springframework.boot.SpringApplication.run(SpringApplication.java:1352)
	at com.example.choquality.proxy.ChoqualityApplicationKt.main(ChoqualityApplication.kt:17)
Caused by: org.springframework.beans.factory.BeanCreationException: Error creating bean with name 'loginService' defined in class path resource [com/example/choquality/common/config/AuthConfig.class]: Could not generate CGLIB subclass of class com.example.choquality.common.config.AuthConfig$loginService$1: Common causes of this problem include using a final class or a non-visible class
	at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.doCreateBean(AbstractAutowireCapableBeanFactory.java:607)
	at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.createBean(AbstractAutowireCapableBeanFactory.java:522)
	at org.springframework.beans.factory.support.AbstractBeanFactory.lambda$doGetBean$0(AbstractBeanFactory.java:337)
	at org.springframework.beans.factory.support.DefaultSingletonBeanRegistry.getSingleton(DefaultSingletonBeanRegistry.java:234)
	at org.springframework.beans.factory.support.AbstractBeanFactory.doGetBean(AbstractBeanFactory.java:335)
	at org.springframework.beans.factory.support.AbstractBeanFactory.getBean(AbstractBeanFactory.java:200)
	at org.springframework.beans.factory.config.DependencyDescriptor.resolveCandidate(DependencyDescriptor.java:254)
	at org.springframework.beans.factory.support.DefaultListableBeanFactory.doResolveDependency(DefaultListableBeanFactory.java:1443)
	at org.springframework.beans.factory.support.DefaultListableBeanFactory.resolveDependency(DefaultListableBeanFactory.java:1353)
	at org.springframework.beans.factory.support.ConstructorResolver.resolveAutowiredArgument(ConstructorResolver.java:904)
	at org.springframework.beans.factory.support.ConstructorResolver.createArgumentArray(ConstructorResolver.java:782)
	... 19 common frames omitted
Caused by: org.springframework.aop.framework.AopConfigException: Could not generate CGLIB subclass of class com.example.choquality.common.config.AuthConfig$loginService$1: Common causes of this problem include using a final class or a non-visible class
	at org.springframework.aop.framework.CglibAopProxy.buildProxy(CglibAopProxy.java:230)
	at org.springframework.aop.framework.CglibAopProxy.getProxy(CglibAopProxy.java:163)
	at org.springframework.aop.framework.ProxyFactory.getProxy(ProxyFactory.java:110)
	at org.springframework.aop.framework.autoproxy.AbstractAutoProxyCreator.buildProxy(AbstractAutoProxyCreator.java:519)
	at org.springframework.aop.framework.autoproxy.AbstractAutoProxyCreator.createProxy(AbstractAutoProxyCreator.java:466)
	at org.springframework.aop.framework.autoproxy.AbstractAutoProxyCreator.wrapIfNecessary(AbstractAutoProxyCreator.java:371)
	at org.springframework.aop.framework.autoproxy.AbstractAutoProxyCreator.postProcessAfterInitialization(AbstractAutoProxyCreator.java:320)
	at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.applyBeanPostProcessorsAfterInitialization(AbstractAutowireCapableBeanFactory.java:438)
	at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.initializeBean(AbstractAutowireCapableBeanFactory.java:1809)
	at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.doCreateBean(AbstractAutowireCapableBeanFactory.java:600)
	... 29 common frames omitted
Caused by: java.lang.IllegalArgumentException: Cannot subclass final class com.example.choquality.common.config.AuthConfig$loginService$1
	at org.springframework.cglib.proxy.Enhancer.generateClass(Enhancer.java:653)
	at org.springframework.cglib.core.DefaultGeneratorStrategy.generate(DefaultGeneratorStrategy.java:26)
	at org.springframework.cglib.core.ClassLoaderAwareGeneratorStrategy.generate(ClassLoaderAwareGeneratorStrategy.java:57)
	at org.springframework.cglib.core.AbstractClassGenerator.generate(AbstractClassGenerator.java:366)
	at org.springframework.cglib.proxy.Enhancer.generate(Enhancer.java:575)
	at org.springframework.cglib.core.AbstractClassGenerator$ClassLoaderData.lambda$new$1(AbstractClassGenerator.java:107)
	at org.springframework.cglib.core.internal.LoadingCache.lambda$createEntry$1(LoadingCache.java:52)
	at java.base/java.util.concurrent.FutureTask.run$$$capture(FutureTask.java:317)
	at java.base/java.util.concurrent.FutureTask.run(FutureTask.java)
	at org.springframework.cglib.core.internal.LoadingCache.createEntry(LoadingCache.java:57)
	at org.springframework.cglib.core.internal.LoadingCache.get(LoadingCache.java:34)
	at org.springframework.cglib.core.AbstractClassGenerator$ClassLoaderData.get(AbstractClassGenerator.java:130)
	at org.springframework.cglib.core.AbstractClassGenerator.create(AbstractClassGenerator.java:317)
	at org.springframework.cglib.proxy.Enhancer.createHelper(Enhancer.java:562)
	at org.springframework.cglib.proxy.Enhancer.createClass(Enhancer.java:407)
	at org.springframework.aop.framework.ObjenesisCglibAopProxy.createProxyClassAndInstance(ObjenesisCglibAopProxy.java:62)
	at org.springframework.aop.framework.CglibAopProxy.buildProxy(CglibAopProxy.java:221)
	... 38 common frames omitted
Disconnected from the target VM, address: '127.0.0.1:63069', transport: 'socket'

Process finished with exit code 1


    @GetMapping(path = ["/deleteAllUser"], produces = [MediaType.APPLICATION_JSON_VALUE])
    fun deleteAllUser(): ResponseEntity<ResponseDto<String>> {

        val result = loginService.deleteAllUsers()

        if(result){
            val body = ResponseDto(
                code = SDKSpec.SUCCESS.code,
                msg = SDKSpec.SUCCESS.message,
                data = SDKSpec.SUCCESS.message
            )
            return ResponseEntity.status(HttpStatus.OK).body(body)
        } else {
            val body = ResponseDto(
                code = SDKSpec.FAIL_DELETE_USER.code,
                msg = SDKSpec.FAIL_DELETE_USER.message,
                data = "DB를 확인해 주세요."
            )
            return ResponseEntity.status(HttpStatus.OK).body(body)
        }
    }

문제 확인 정리해줘
````
````
@PostMapping(path = ["/save"], produces = [MediaType.APPLICATION_JSON_VALUE])
    fun saveUser(
        @RequestBody requestBody: Map<String, String>
    ): ResponseEntity<ResponseDto<String>> {

        try {
            val email = requestBody["email"] ?: throw SDKException(SDKSpec.FAIL_SAVE_USER)
            val username = requestBody["name"] ?: email
            val password = requestBody["password"]?: throw SDKException(SDKSpec.FAIL_SAVE_USER)    
        } catch (e : SDKException){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body)
        }
        

        loginService.saveUser(email,username,password)
        val body = ResponseDto(
            code = SDKSpec.SUCCESS.code,
            msg = SDKSpec.SUCCESS.message,
            data = SDKSpec.SUCCESS.message
        )
        return ResponseEntity.status(HttpStatus.OK).body(body)
    }

리펙토링해줘
````
````
@ControllerAdvice
class GlobalControllerExceptionHandler {

    @ExceptionHandler(value = [NullPointerException::class, SecurityException::class, SDKException::class])
    fun handleForbidden(): ModelAndView =
        ModelAndView("error/403").apply { status = HttpStatus.FORBIDDEN }

    @ExceptionHandler(NoHandlerFoundException::class)
    fun handleNotFound(): ModelAndView =
        ModelAndView("error/404").apply { status = HttpStatus.NOT_FOUND }

    @ExceptionHandler(Exception::class)
    fun handleAll(e: Exception): ModelAndView =
        ModelAndView("error/500").apply { status = HttpStatus.INTERNAL_SERVER_ERROR }
}

난 아래 구문에 디버깅이 걸릴거라 생각했는데 안되네
  @ExceptionHandler(value = [NullPointerException::class, SecurityException::class, SDKException::class])
    fun handleForbidden(): ModelAndView =
        ModelAndView("error/403").apply { status = HttpStatus.FORBIDDEN }
2025-08-18 14:29:26 | DEBUG | org.springframework.security.web.FilterChainProxy | doFilterInternal | Securing GET /testtet 
2025-08-18 14:29:26 | DEBUG | org.springframework.security.web.authentication.AnonymousAuthenticationFilter | defaultWithAnonymous | Set SecurityContextHolder to anonymous SecurityContext 
2025-08-18 14:29:27 | DEBUG | org.springframework.security.web.authentication.Http403ForbiddenEntryPoint | commence | Pre-authenticated entry point called. Rejecting access 
````
````
 @GetMapping(produces = [MediaType.APPLICATION_JSON_VALUE])
    fun getTodos(
        @AuthenticationPrincipal user: ChoqualityUser
    ): ResponseEntity<ResponseDto<String>> {
        val id = user.loginInfo.id ?: SDKException(SDKSpec.FAIL_LOGIN)        
        val todoList  = todoService.getTodoList(id);
        val body = ResponseDto(
            code = SDKSpec.SUCCESS.code,
            msg = SDKSpec.SUCCESS.message,
            data = ""
        )
        return ResponseEntity.status(HttpStatus.OK).body(body)
    }

수정해줘
````
````
override fun getTodo(userId: Int, todoId: Int): TodoInfoEntity {
        val probe = UserTodoEntity().apply {
            id = UserTodoId(userId = userId, todoId = todoId)
        }
        val matcher = ExampleMatcher.matchingAll()
            .withIgnoreNullValues()
            .withIgnoreCase()
        val example = Example.of(probe, matcher)
        val userTodoEntity = userTodoRepository.findOne(example)
        if(userTodoEntity.isPresent){
            val todo = userTodoEntity.get().todo
            return todo   
        } 
        else
            throw SDKException(SDKSpec.FAIL_TODO_GET)
    }
수정해줘
````
````
코틀린에 스프링 부트로 했는데 @Test를 작성해야 하는데 헤더 값에 토큰을 넣어야 하는 예시
````
