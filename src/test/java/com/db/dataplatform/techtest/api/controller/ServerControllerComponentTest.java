package com.db.dataplatform.techtest.api.controller;

import com.db.dataplatform.techtest.TestDataHelper;
import com.db.dataplatform.techtest.server.api.controller.ServerController;
import com.db.dataplatform.techtest.server.api.model.DataEnvelope;
import com.db.dataplatform.techtest.server.api.model.ErrorResponse;
import com.db.dataplatform.techtest.server.component.Server;
import com.db.dataplatform.techtest.server.persistence.BlockTypeEnum;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;

import static com.db.dataplatform.techtest.server.Constants.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ServerController.class)
public class ServerControllerComponentTest {

	public static final String URI_PUSHDATA = "/dataserver/pushdata";
	public static final String URI_GETDATA_BY_BLOCKTYPE = "/dataserver/data/{blockType}";
	public static final String URI_PATCH_DATA = "/dataserver/update/{name}/{newBlockType}";

	private final DataEnvelope testDataEnvelope = TestDataHelper.createTestDataEnvelopeApiObject();
	private final String blockName = testDataEnvelope.getDataHeader().getName();
	private final String CHECKSUM = TestDataHelper.getMd5Checksum(TestDataHelper.DUMMY_DATA);
	private final String INCORRECT_CHECKSUM = "incorrect-checksum";

	@Captor
	ArgumentCaptor<DataEnvelope> dataEnvelopeCaptor;
	@Captor
	ArgumentCaptor<String> checksumCaptor;
	@Captor
	ArgumentCaptor<String> blockNameCaptor;
	@Captor
	ArgumentCaptor<BlockTypeEnum> blockTypeCaptor;

	@MockBean
	private Server serverMock;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private MockMvc mockMvc;

	public ServerControllerComponentTest() throws NoSuchAlgorithmException {
	}


	@BeforeEach
	public void setUp() throws NoSuchAlgorithmException, IOException, ExecutionException, InterruptedException {
		// mock service calls for : push-data, get-data, update-data
		when(serverMock.saveDataEnvelope(testDataEnvelope, CHECKSUM))
				.thenReturn(true);
		when(serverMock.saveDataEnvelope(testDataEnvelope, "incorrect-checksum"))
				.thenReturn(false);
		when(serverMock.getDataEnevelopeByBlocktype(BlockTypeEnum.BLOCKTYPEA))
				.thenReturn(Arrays.asList(testDataEnvelope));
		when(serverMock.updateBlockType(blockName, BlockTypeEnum.BLOCKTYPEB))
				.thenReturn(true);
	}

	@Test
	public void pushDataReturnsTrue_whenDataEnvelopeAndChecksumIsValid() throws Exception {

		String testDataEnvelopeJson = objectMapper.writeValueAsString(testDataEnvelope);

		mockMvc.perform(post(URI_PUSHDATA)
				.param("checksum", CHECKSUM)
				.content(testDataEnvelopeJson)
				.contentType(MediaType.APPLICATION_JSON_VALUE)
		)
		.andExpect(status().isOk())
		.andExpect(content().string("true"));

		verify(serverMock).saveDataEnvelope(dataEnvelopeCaptor.capture(), checksumCaptor.capture());

		assertThat(dataEnvelopeCaptor.getValue()).isEqualToComparingFieldByField(testDataEnvelope);
		assertThat(checksumCaptor.getValue()).isEqualTo(CHECKSUM);

	}

	@Test
	public void pushDataReturnsFalse_whenDataEnvelopIsValidButChecksumIsInvalid() throws Exception {

		String testDataEnvelopeJson = objectMapper.writeValueAsString(testDataEnvelope);

		mockMvc.perform(post(URI_PUSHDATA)
				.param("checksum", INCORRECT_CHECKSUM)
				.content(testDataEnvelopeJson)
				.contentType(MediaType.APPLICATION_JSON_VALUE)
		)
		.andExpect(status().isOk())
		.andExpect(content().string("false"));

		verify(serverMock).saveDataEnvelope(dataEnvelopeCaptor.capture(), checksumCaptor.capture());

		assertThat(dataEnvelopeCaptor.getValue()).isEqualToComparingFieldByField(testDataEnvelope);
		assertThat(checksumCaptor.getValue()).isEqualTo(INCORRECT_CHECKSUM);

	}

	@Test
	public void pushDataReturns400AndValidationError_whenDataHeaderNameIsBlank() throws Exception {

		testDataEnvelope.getDataHeader().setName("");
		String testDataEnvelopeJson = objectMapper.writeValueAsString(testDataEnvelope);

		ErrorResponse errorResponse = new ErrorResponse(400, VALIDATION_ERROR);
		errorResponse.addFieldError("dataHeader.name", BLANK_BLOCK_NAME);

		mockMvc.perform(post(URI_PUSHDATA)
				.param("checksum", CHECKSUM)
				.content(testDataEnvelopeJson)
				.contentType(MediaType.APPLICATION_JSON_VALUE)
		)
		.andExpect(status().isBadRequest())
		.andExpect(content().json(objectMapper.writeValueAsString(errorResponse)));

		verify(serverMock, never()).saveDataEnvelope(any(), any());

	}

	@Test
	public void getDataByBlocktypeReturnsDataEnvelope_whenInputIsValid() throws Exception {
		// json array
		String testDataEnvelopeJson = "[" + objectMapper.writeValueAsString(testDataEnvelope) + "]";

		mockMvc.perform(get(URI_GETDATA_BY_BLOCKTYPE, "BLOCKTYPEA")
				.contentType(MediaType.APPLICATION_JSON_VALUE)
		)
		.andExpect(status().isOk())
		.andExpect(content().json(testDataEnvelopeJson));

		verify(serverMock).getDataEnevelopeByBlocktype(blockTypeCaptor.capture());
		assertThat(blockTypeCaptor.getValue()).isEqualTo(BlockTypeEnum.BLOCKTYPEA);

	}

	@Test
	public void getDataByBlocktypeReturns400AndValidationError_whenBlockTypeIsInvalid() throws Exception {
		// json array
		String testDataEnvelopeJson = "[" + objectMapper.writeValueAsString(testDataEnvelope) + "]";

		mockMvc.perform(get(URI_GETDATA_BY_BLOCKTYPE, "nonExistentBlockType")
					.contentType(MediaType.APPLICATION_JSON_VALUE)
		)
		.andExpect(status().isBadRequest())
		.andExpect(content().string(INVALID_BLOCK_TYPE));

		verify(serverMock, never()).getDataEnevelopeByBlocktype(any());

	}

	@Test
	public void updateDataReturnsOK_whenInputIsValid() throws Exception {

		mockMvc.perform(patch(URI_PATCH_DATA, blockName, "BLOCKTYPEB")
					.contentType(MediaType.APPLICATION_JSON_VALUE)
		)
		.andExpect(status().isOk())
		.andExpect(content().string("true"));

		verify(serverMock).updateBlockType(blockNameCaptor.capture(), blockTypeCaptor.capture());
		assertThat(blockNameCaptor.getValue()).isEqualTo(blockName);
		assertThat(blockTypeCaptor.getValue()).isEqualTo(BlockTypeEnum.BLOCKTYPEB);

	}

	@Test
	public void updateDataReturns400AndValidationError_whenBlockTypeIsInvalid() throws Exception {

		mockMvc.perform(patch(URI_PATCH_DATA, blockName, "nonExistentBlockType")
					.contentType(MediaType.APPLICATION_JSON_VALUE)
		)
		.andExpect(status().isBadRequest())
		.andExpect(content().string(INVALID_BLOCK_TYPE));

		verify(serverMock, never()).updateBlockType(any(), any());

	}

	@Test
	public void updateDataReturnsFalse_whenBlockNameDoesNotExist() throws Exception {

		mockMvc.perform(patch(URI_PATCH_DATA, "nonExistentBlockName", "BLOCKTYPEB")
					.contentType(MediaType.APPLICATION_JSON_VALUE)
		)
		.andExpect(status().isOk())
		.andExpect(content().string("false"));

		verify(serverMock).updateBlockType(blockNameCaptor.capture(), blockTypeCaptor.capture());
		assertThat(blockNameCaptor.getValue()).isEqualTo("nonExistentBlockName");
		assertThat(blockTypeCaptor.getValue()).isEqualTo(BlockTypeEnum.BLOCKTYPEB);

	}

}
