package com.webshopping.listener;

import com.webshopping.stats.SiteStats;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import jakarta.servlet.http.HttpSessionEvent;
import jakarta.servlet.http.HttpSessionListener;

@WebListener
public class SiteStatsListener implements ServletContextListener, HttpSessionListener {
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        SiteStats.onStartup();
    }

    @Override
    public void sessionCreated(HttpSessionEvent se) {
        SiteStats.onSessionCreated();
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent se) {
        SiteStats.onSessionDestroyed();
    }
}
