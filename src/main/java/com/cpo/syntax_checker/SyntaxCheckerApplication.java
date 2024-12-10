package com.cpo.syntax_checker;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.awt.Desktop;
import java.net.URI;

@SpringBootApplication
public class SyntaxCheckerApplication {

	public static void main(String[] args) {
		SpringApplication.run(SyntaxCheckerApplication.class, args);
	}

	@Bean
	public CommandLineRunner openBrowser() {
		return args -> {
			try {
				String url = "http://localhost:2424"; // Adjust the URL if necessary
				if (Desktop.isDesktopSupported()) {
					Desktop.getDesktop().browse(new URI(url));
				} else {
					String os = System.getProperty("os.name").toLowerCase();
					if (os.contains("mac")) {
						Runtime.getRuntime().exec("open " + url);
					} else if (os.contains("nix") || os.contains("nux")) {
						Runtime.getRuntime().exec("xdg-open " + url);
					} else if (os.contains("win")) {
						Runtime.getRuntime().exec("cmd /c start " + url);
					} else {
						throw new UnsupportedOperationException("Unsupported operating system: " + os);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		};
	}}
