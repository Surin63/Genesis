package com.engeto.GenesisResources;

import com.engeto.GenesisResources.domain.UserInfo;
import com.engeto.GenesisResources.model.UserInfoDTO;
import com.engeto.GenesisResources.repository.UserInfoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;

import java.util.Objects;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpMethod.PUT;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class GenesisResourcesApplicationTests {


	@LocalServerPort
	private int port;

	@Autowired
	private TestRestTemplate restTemplate;

	@Autowired
	private UserInfoRepository userInfoRepository;

	@BeforeEach
	void setUp() {
		userInfoRepository.deleteAll();
	}

	@Test
	void GIVEN_empty_DB_WHEN_called_all_request_methods_THEN_all_request_methods_worked_correctly() {
		//GIVEN Empty DB
		assertThat(userInfoRepository.findAll()).isEmpty();

		//WHEN Create
		UserInfoDTO oneUserInfoDTO = new UserInfoDTO("Jack", "Johnson", "654321987456", "someUuid");
		ResponseEntity<UserInfoDTO> createdUserInfoDTO = restTemplate.postForEntity("http://localhost:" + port + "/api/v1/user", oneUserInfoDTO, UserInfoDTO.class);

		assertThat(userInfoRepository.findAll()).hasSize(1);

		//WHEN  Get_By_Id
		ResponseEntity<UserInfoDTO> returneduserInfo = restTemplate.getForEntity(
				"http://localhost:" + port + "/api/v1/user/{id}",
				UserInfoDTO.class, Objects.requireNonNull(createdUserInfoDTO.getBody()).getId());

		assertThat(returneduserInfo).isNotNull();

		//WHEN Update
		oneUserInfoDTO.setName("updatedName");
		oneUserInfoDTO.setId(createdUserInfoDTO.getBody().getId());
		restTemplate.exchange(
				"http://localhost:" + port + "/api/v1/user", PUT, new HttpEntity<>(oneUserInfoDTO),
				HttpStatus.class);

		Optional<UserInfo> userInfoDB = userInfoRepository.findById(createdUserInfoDTO.getBody().getId());
		assertThat(userInfoDB).isPresent();
		UserInfo updatedUserInfo = userInfoDB.orElseThrow();
		assertThat(updatedUserInfo.getName()).isEqualTo("updatedName");



		//WHEN Delete
		restTemplate.exchange("http://localhost:" + port + "/api/v1/user/{id}", HttpMethod.DELETE,
				null, Void.class, Objects.requireNonNull(createdUserInfoDTO.getBody().getId()));

		//THEN
		assertThat(userInfoRepository.findById(createdUserInfoDTO.getBody().getId())).isEmpty();
		assertThat(userInfoRepository.findAll()).isEmpty();

	}

	@Test
	public void testErrorHandlingReturnsBadRequest() {
		String url = "http://localhost:" + port + "/wrong";

		try {
			restTemplate.getForEntity(url, String.class);
		} catch (HttpClientErrorException e) {
			assertThat(e.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
		}
	}
}




