package com.voncken.lostandfound;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.JdbcTemplate;

import com.voncken.lostandfound.Contracts.ILostItemService;
import com.voncken.lostandfound.Contracts.IStorageService;
import com.voncken.lostandfound.Contracts.IUserClaimService;
import com.voncken.lostandfound.Services.StorageProperties;

@SpringBootApplication
@EnableConfigurationProperties(StorageProperties.class)
public class LostandfoundApplication {

	@Autowired
	private JdbcTemplate jdbcTemplate;

	public static void main(String[] args) {
		SpringApplication.run(LostandfoundApplication.class, args);
	}

	@Bean
	CommandLineRunner init(IStorageService storageService, IUserClaimService userClaimService, ILostItemService lostItemService) {

		jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS \"LostItem\" (\r\n" + //
				"\t\"Guid\"\tTEXT UNIQUE,\r\n" + //
				"\t\"Name\"\tTEXT,\r\n" + //
				"\t\"Quantity\"\tINTEGER,\r\n" + //
				"\t\"LostAndFoundPlace\"\tTEXT,\r\n" + //
				"\tPRIMARY KEY(\"Guid\")\r\n" + //
				");");

		jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS \"UserClaim\" (\r\n" + //
				"\t\"Guid\"\tTEXT UNIQUE,\r\n" + //
				"\t\"LostItemGuid\"\tTEXT,\r\n" + //
				"\t\"ClaimedQuantity\"\tINTEGER,\r\n" + //
				"\t\"UserId\"\tTEXT,\r\n" + //
				"\tPRIMARY KEY(\"Guid\")\r\n" + //
				");");
		

		return (args) -> {
			lostItemService.init();
			userClaimService.init();
			storageService.init();
		};
	}
}
