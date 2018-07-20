package by.spalex.diplom.snmp.configuration;

import by.spalex.diplom.snmp.model.Role;
import by.spalex.diplom.snmp.model.User;
import by.spalex.diplom.snmp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.util.HashSet;
import java.util.Set;


@Configuration
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    private final AccessDeniedHandler accessDeniedHandler;

    private final AuthenticationSuccessHandler successHandler;
    private final DataSource dataSource;
    private final UserService userService;
    @Value("${spring.queries.users-query}")
    private String usersQuery;
    @Value("${spring.queries.roles-query}")
    private String rolesQuery;
    @Value("${spring.security.admin.name}")
    private String adminLogin;
    @Value("${spring.security.admin.password}")
    private String adminPass;

    @Autowired
    public SecurityConfiguration(BCryptPasswordEncoder bCryptPasswordEncoder, AccessDeniedHandler accessDeniedHandler,
                                 AuthenticationSuccessHandler successHandler, DataSource dataSource, UserService userService) {
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.accessDeniedHandler = accessDeniedHandler;
        this.successHandler = successHandler;
        this.dataSource = dataSource;
        this.userService = userService;
    }

    @PostConstruct
    protected void init() {
        User admin = userService.findOneByEmail(adminLogin);
        if (admin == null) {
            admin = new User();
            admin.setEmail(adminLogin);
            admin.setPassword(adminPass);
            Set<Role> roleSet = new HashSet<>();
            roleSet.add(Role.ADMIN);
            admin.setRoles(roleSet);
            admin.setName("ADMIN");
            userService.save(admin);
        }
        User user = userService.findOneByEmail("user@localhost");
        if (user == null) {
            user = new User();
            user.setEmail("user@localhost");
            user.setPassword("123");
            Set<Role> roleSet = new HashSet<>();
            roleSet.add(Role.USER);
            user.setRoles(roleSet);
            user.setName("Test user");
            userService.save(user);
        }
    }


    @Override
    protected void configure(AuthenticationManagerBuilder auth)
            throws Exception {
        auth.jdbcAuthentication().passwordEncoder(bCryptPasswordEncoder)
                .usersByUsernameQuery(usersQuery)
                .authoritiesByUsernameQuery(rolesQuery)
                .dataSource(dataSource);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                .antMatchers("/*", "/api/*").permitAll()
                .antMatchers("admin/**").hasAnyAuthority("ADMIN")
                .antMatchers("user/**").hasAnyAuthority("USER", "ADMIN")
                .anyRequest()
                .authenticated().and().csrf().disable().formLogin()
                .loginPage("/login").failureUrl("/login?error=true")
                .successHandler(successHandler)
                .usernameParameter("email")
                .passwordParameter("password")
                .and().logout()
                .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                .logoutSuccessUrl("/").and()
                .exceptionHandling().accessDeniedHandler(accessDeniedHandler);

    }

    @Override
    public void configure(WebSecurity web) {
        web.ignoring().antMatchers("/webjars/**", "/resources/**", "/static/**", "/css/**", "/js/**", "/images/**");
    }

}