package com.example.securitydemo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import javax.sql.DataSource;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableMethodSecurity
@EnableWebSecurity
public class SecurityConfig {
    @Autowired
    DataSource dataSource;

    @Bean
    SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) {
        http.authorizeHttpRequests((requests) ->
                requests.requestMatchers("/h2-console/**").permitAll()
                .anyRequest().authenticated());
        http.sessionManagement((session) ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));//through this we are making stateless request
        // http.formLogin(withDefaults()); //formbased contain sessionID and headers along with payload as csrf token
        // ADD THESE TWO LINES
        http.csrf(csrf -> csrf.disable());//when this line was not there in h2-console url the sign in pop up was showing again and again
        http.headers(headers -> headers.frameOptions(frame -> frame.disable()));

        http.httpBasic(withDefaults());//but this contain sessionID and headers only no csrf token
        return http.build();
    }//if request is containing cookie means it is stateful

    @Bean
    public UserDetailsService userDetailsService() {//UserDetailsService needed to load data,
        // It is a core interface used to load user information during authentication.
        UserDetails user = User.withUsername("user1")
                .password("{noop}userPassword1")
                .roles("USER")
                .build();

        UserDetails admin = User.withUsername("admin1")
                .password("{noop}adminPassword1")
                .roles("ADMIN")
                .build();

        //JdbcUserDetailsManager is used when you want
        // users and roles stored in a database instead of memory.
        JdbcUserDetailsManager userDetailsManager
                = new JdbcUserDetailsManager(dataSource);
        //JdbcUserDetailsManager → uses → DataSource → to connect with → Database
        //Datasource is an interface in JDBC that represents a database connection provider.
        userDetailsManager.createUser(user);
        userDetailsManager.createUser(admin);
        return userDetailsManager;

//        return new InMemoryUserDetailsManager(user, admin);
        //InMemoryUserDetailsManager needs object of type userDetails hence we created UserDetails type object
    }
    
}
/*
UserDetailsService(parent)
        ↑
UserDetailsPasswordService(sub)
        ↑
UserDetailsManager(sub}

        UserDetailsManager
   ├── InMemoryUserDetailsManager
   ├── JdbcUserDetailsManager
   └── LdapUserDetailsManager
*/
/*
1️⃣ UserDetails (User Data Object)
What it is

An interface representing a user.

It stores authentication data.

Contains

username

password

roles/authorities

account status

Example implementation class:

User (class in Spring Security)

Example object:

UserDetails user = User.withUsername("user1")
        .password("{noop}password")
        .roles("USER")
        .build();
Why needed

Spring Security needs a standard format to represent a user.

2️⃣ UserDetailsService (Fetch User)
What it is

An interface used to load users during login.

Main method:

UserDetails loadUserByUsername(String username);
Why needed

When a user logs in, Spring Security must fetch the user details.

Flow:

login request
     ↓
UserDetailsService
     ↓
returns UserDetails

3️⃣ UserDetailsManager (Manage Users)
What it is

An extension of UserDetailsService.

Hierarchy:

UserDetailsService
        ↑
UserDetailsManager
Additional features

Besides loading users, it can:

create user

update user

delete user

change password

Example methods:

createUser()
updateUser()
deleteUser()
changePassword()
Why needed

To manage users dynamically.

4️⃣ JdbcUserDetailsManager (Database Implementation)
What it is

A class implementing UserDetailsManager.

Hierarchy:

UserDetailsService
        ↑
UserDetailsManager
        ↑
JdbcUserDetailsManager
Purpose

Stores users in a database using JDBC.

It needs DataSource to connect to the database.

Example:

JdbcUserDetailsManager manager =
        new JdbcUserDetailsManager(dataSource);
5️⃣ Relationship Between All Four

Systematic flow:

UserDetails
    ↓
UserDetailsService
    ↓
UserDetailsManager
    ↓
JdbcUserDetailsManager

Explanation:

Component	Role
UserDetails	User data
UserDetailsService	Fetch user for login
UserDetailsManager	Fetch + manage users
JdbcUserDetailsManager	Database implementation
6️⃣ Complete Authentication Flow
User login request
       ↓
Spring Security
       ↓
UserDetailsService
       ↓
JdbcUserDetailsManager
       ↓
Database
       ↓
UserDetails object
       ↓
Password verification
       ↓
Authentication success

7️⃣ One-Line Summary
Component	Simple Meaning
UserDetails	User information
UserDetailsService	Loads user during login
UserDetailsManager	Loads + manages users
JdbcUserDetailsManager	Stores users in database

✅ Very short memory trick

UserDetails → user data
UserDetailsService → find user
UserDetailsManager → manage user
JdbcUserDetailsManager → manage user in database

If you want, I can also show a very clear diagram of the complete Spring Security authentication architecture (filters → authentication manager → providers → user service) which makes the whole system 100× easier to understand.
 */
