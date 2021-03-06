package com.smartsheet.api.internal;

/*
 * #[license]
 * Smartsheet SDK for Java
 * %%
 * Copyright (C) 2014 Smartsheet
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * %[license]
 */

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.junit.Before;
import org.junit.Test;

import com.smartsheet.api.SmartsheetException;
import com.smartsheet.api.internal.http.DefaultHttpClient;
import com.smartsheet.api.models.AccessLevel;
import com.smartsheet.api.models.Column;
import com.smartsheet.api.models.ColumnType;
import com.smartsheet.api.models.FormatDetails;
import com.smartsheet.api.models.ObjectInclusion;
import com.smartsheet.api.models.PaperSize;
import com.smartsheet.api.models.Sheet;
import com.smartsheet.api.models.SheetEmail;
import com.smartsheet.api.models.SheetEmailFormat;
import com.smartsheet.api.models.SheetPublish;
import com.smartsheet.api.models.format.Color;
import com.smartsheet.api.models.format.FontSize;
import com.smartsheet.api.models.format.VerticalAlignment;


public class SheetResourcesImplTest extends ResourcesImplBase {
	SheetResourcesImpl sheetResource;

	@Before
	public void setUp() throws Exception {
		// Create a folder resource
		sheetResource = new SheetResourcesImpl(new SmartsheetImpl("http://localhost:9090/1.1/", "accessToken",
				new DefaultHttpClient(), serializer));
	}

	@Test
	public void testListSheets() throws SmartsheetException, IOException {

		server.setResponseBody(new File("src/test/resources/listSheets.json"));

		List<Sheet> sheets = sheetResource.listSheets();
		assertEquals(2, sheets.size());
	}

	@Test
	public void testListOrganizationSheets() throws SmartsheetException, IOException {

		server.setResponseBody(new File("src/test/resources/listSheets.json"));
		List<Sheet> sheets = sheetResource.listOrganizationSheets();
		assertEquals(2, sheets.size());
	}

	@Test
	public void testGetSheet() throws SmartsheetException, IOException {

		server.setResponseBody(new File("src/test/resources/getSheet.json"));
		Sheet sheet = sheetResource.getSheet(123123L, null);
		assertEquals(9,sheet.getColumns().size());
		assertEquals(0,sheet.getRows().size());
		
		sheet = sheetResource.getSheet(123123L, EnumSet.allOf(ObjectInclusion.class));
		assertEquals(9,sheet.getColumns().size());
		assertEquals(0,sheet.getRows().size());
	}
	@Test
	public void testGetSheetWithFormat() throws SmartsheetException, IOException {
		
		server.setResponseBody(new File("src/test/resources/getSheetWithFormat.json"));
		Sheet sheet = sheetResource.getSheet(123123L, null);
		
		assertNotNull(sheet.getColumnByIndex(0).getFormat());
		assertEquals(VerticalAlignment.TOP, sheet.getColumnByIndex(0).getFormat().getVerticalAlignment());
		
		assertNotNull(sheet.getRowByRowNumber(1).getFormat());
		assertEquals(FontSize.PT_12, sheet.getRowByRowNumber(1).getFormat().getFontSize());

		assertNotNull(sheet.getRowByRowNumber(1).getCells().get(0).getFormat());
		assertEquals(Color.YELLOW_3, sheet.getRowByRowNumber(1).getCells().get(0).getFormat().getBackgroundColor());
		
		
	}

	@Test
	public void testGetSheetAsExcel() throws SmartsheetException, IOException {
		File file = new File("src/test/resources/getExcel.xls");
		server.setResponseBody(file);
		server.setContentType("application/vnd.ms-excel");

		ByteArrayOutputStream output = new ByteArrayOutputStream();
		sheetResource.getSheetAsExcel(1234L, output);
		
		assertNotNull(output);
		assertTrue(output.toByteArray().length > 0);
		
		//byte[] original = IOUtils.toByteArray(new FileReader(file));
		byte[] data = Files.readAllBytes(Paths.get(file.getPath()));
		assertEquals(data.length, output.toByteArray().length);
	}

	@Test
	public void testGetSheetAsPDF() throws SmartsheetException, IOException {

		File file = new File("src/test/resources/getPDF.pdf");
		server.setResponseBody(file);
		server.setContentType("application/pdf");

		ByteArrayOutputStream output = new ByteArrayOutputStream();

		sheetResource.getSheetAsPDF(1234L, output, null);
		
		assertNotNull("Downloaded PDF is null.",output);
		assertTrue("Downloaded PDF is empty.", output.toByteArray().length > 0);
		assertEquals("Downloaded PDF does not match the original size.",107906,output.toByteArray().length);
		
		//test a larger PDF
		file = new File("src/test/resources/large_sheet.pdf");
		server.setResponseBody(file);
		server.setContentType("application/pdf");
		output = new ByteArrayOutputStream();
		sheetResource.getSheetAsPDF(1234L, output, PaperSize.LEGAL);
		assertNotNull("Downloaded PDF is null.",output);
		assertTrue("Downloaded PDF is empty.", output.toByteArray().length > 0);
		assertEquals("Downloaded PDF does not match the original size.",936995,output.toByteArray().length);
	}

	@Test
	public void testCreateSheet() throws SmartsheetException, IOException {
		server.setResponseBody(new File("src/test/resources/createSheet.json"));

		Sheet sheet = new Sheet();
		sheet.setName("NEW TEST SHEET");
		ArrayList<Column> list = new ArrayList<Column>();
		Column col = new Column();
		col.setPrimary(true);
		col.setTitle("column1");
		col.setType(ColumnType.TEXT_NUMBER);
		list.add(col);
		col = new Column();
		col.setTitle("column2");
		col.setType(ColumnType.TEXT_NUMBER);
		list.add(col);

		sheet.setColumns(list);
		Sheet newSheet = sheetResource.createSheet(sheet);

		if (newSheet.getColumns().size() != 2) {
			fail("Issue creating a sheet");
		}
	}

	@Test
	public void testCreateSheetFromExisting() throws SmartsheetException, IOException {

		server.setResponseBody(new File("src/test/resources/createSheetFromExisting.json"));

		Sheet sheet = new Sheet();
		sheet.setFromId(2906571706525572L);
		Sheet newSheet = sheetResource.createSheetFromExisting(sheet, EnumSet.allOf(ObjectInclusion.class));

		assertEquals(466343087630212L, newSheet.getId().longValue());
		assertEquals(AccessLevel.OWNER, newSheet.getAccessLevel());
		assertEquals("https://app.smartsheet.com/b/home?lx=asdf",newSheet.getPermalink());

		newSheet = sheetResource.createSheetFromExisting(sheet, null);
		assertEquals(466343087630212L, newSheet.getId().longValue());
		assertEquals(AccessLevel.OWNER, newSheet.getAccessLevel());
		assertEquals("https://app.smartsheet.com/b/home?lx=asdf",newSheet.getPermalink());

	}

	@Test
	public void testCreateSheetInFolder() throws SmartsheetException, IOException {
		server.setResponseBody(new File("src/test/resources/createSheet.json"));

		Sheet sheet = new Sheet();
		sheet.setName("NEW TEST SHEET");
		ArrayList<Column> list = new ArrayList<Column>();
		Column col = new Column();
		col.setPrimary(true);
		col.setTitle("column1");
		col.setType(ColumnType.TEXT_NUMBER);
		list.add(col);
		col = new Column();
		col.setTitle("column2");
		col.setType(ColumnType.TEXT_NUMBER);
		col.setId(4049365800118148L);
		list.add(col);

		sheet.setColumns(list);
		Sheet newSheet = sheetResource.createSheetInFolder(12345L, sheet);

		assertEquals(2, newSheet.getColumns().size());
		assertEquals(col,newSheet.getColumnByIndex(1));
		assertNotEquals(col, newSheet.getColumnByIndex(0));
		assertNull((new Sheet()).getColumnByIndex(100));
		assertEquals(col,newSheet.getColumnById(4049365800118148L));
		assertNotEquals(col,newSheet.getColumnById(4032471613368196L));
		assertNull((new Sheet()).getColumnById(100));
	}

	@Test
	public void testCreateSheetInFolderFromExisting() throws SmartsheetException, IOException {

		server.setResponseBody(new File("src/test/resources/createSheetFromExisting.json"));

		Sheet sheet = new Sheet();
		sheet.setFromId(2906571706525572L);
		Sheet newSheet = sheetResource.createSheetInFolderFromExisting(1234L, sheet,
				EnumSet.allOf(ObjectInclusion.class));

		if (newSheet.getId().toString().isEmpty() || newSheet.getAccessLevel() != AccessLevel.OWNER
				|| newSheet.getPermalink().toString().isEmpty()) {
			fail("Sheet not correctly copied");
		}

		newSheet = sheetResource.createSheetInFolderFromExisting(1234L, sheet, null);
	}

	@Test
	public void testCreateSheetInWorkspace() throws SmartsheetException, IOException {
		server.setResponseBody(new File("src/test/resources/createSheet.json"));

		Sheet sheet = new Sheet();
		sheet.setName("NEW TEST SHEET");
		ArrayList<Column> list = new ArrayList<Column>();
		Column col = new Column();
		col.setPrimary(true);
		col.setTitle("column1");
		col.setType(ColumnType.TEXT_NUMBER);
		list.add(col);
		col = new Column();
		col.setTitle("column2");
		col.setType(ColumnType.TEXT_NUMBER);
		list.add(col);

		sheet.setColumns(list);
		Sheet newSheet = sheetResource.createSheetInWorkspace(1234L, sheet);
		assertEquals(2, newSheet.getColumns().size());
	}

	@Test
	public void testCreateSheetInWorkspaceFromExisting() throws SmartsheetException, IOException {
		server.setResponseBody(new File("src/test/resources/createSheetFromExisting.json"));

		Sheet sheet = new Sheet();
		sheet.setFromId(2906571706525572L);
		Sheet newSheet = sheetResource.createSheetInWorkspaceFromExisting(1234L, sheet,
				EnumSet.allOf(ObjectInclusion.class));

		assertEquals(466343087630212L, newSheet.getId().longValue());
		assertEquals(AccessLevel.OWNER, newSheet.getAccessLevel());
		assertEquals("https://app.smartsheet.com/b/home?lx=asdf",newSheet.getPermalink());

		newSheet = sheetResource.createSheetInWorkspaceFromExisting(1234L, sheet, null);
	}

	@Test
	public void testDeleteSheet() throws SmartsheetException, IOException {
		server.setResponseBody(new File("src/test/resources/deleteSheet.json"));
		sheetResource.deleteSheet(1234L);
	}

	@Test
	public void testUpdateSheet() throws SmartsheetException, IOException {
		server.setResponseBody(new File("src/test/resources/updateSheet.json"));

		Sheet sheet = new Sheet();
		sheet.setName("new name");
		sheet.setId(1234L);
		Sheet newSheet = sheetResource.updateSheet(sheet);

		assertEquals("Sheet update (rename) failed.", "new name", newSheet.getName());
	}

	@Test
	public void testGetSheetVersion() throws SmartsheetException, IOException {
		server.setResponseBody(new File("src/test/resources/getSheetVersion.json"));
		int version = sheetResource.getSheetVersion(1234L);
		if (version != 1) {
			fail("Issue getting sheet version");
		}
	}

	@Test
	public void testSendSheet() throws SmartsheetException, IOException {
		server.setResponseBody(new File("src/test/resources/sendEmails.json"));
		
		String[] emailAddress = { "someemail@somewhere.com" };
		SheetEmail email = new SheetEmail();
		email.setFormat(SheetEmailFormat.PDF);
		FormatDetails format = new FormatDetails();
		format.setPaperSize(PaperSize.A0);
		email.setFormatDetails(format);
		email.setTo(Arrays.asList(emailAddress));
		sheetResource.sendSheet(1234L, email);
	}

	@Test
	public void testShares() {
		sheetResource.shares();
	}

	@Test
	public void testRows() {
		sheetResource.rows();
	}

	@Test
	public void testColumns() {
		sheetResource.columns();
	}

	@Test
	public void testAttachments() {
		sheetResource.attachments();
	}

	@Test
	public void testDiscussions() {
		sheetResource.discussions();
	}

	@Test
	public void testGetPublishStatus() throws SmartsheetException, IOException {
		server.setResponseBody(new File("src/test/resources/getPublishStatus.json"));

		SheetPublish publishStatus = sheetResource.getPublishStatus(1234L);

		if (publishStatus == null || publishStatus.getReadOnlyFullEnabled() != false
				|| publishStatus.getIcalEnabled() != false || publishStatus.getReadOnlyLiteEnabled() != false
				|| publishStatus.getReadWriteEnabled() != false) {

			fail("Issue creating the publish status object");
		}
	}

	@Test
	public void testUpdatePublishStatus() throws SmartsheetException, IOException {

		server.setResponseBody(new File("src/test/resources/setPublishStatus.json"));

		SheetPublish publish = new SheetPublish();
		publish.setIcalEnabled(true);
		publish.setReadOnlyFullEnabled(true);
		publish.setReadOnlyLiteEnabled(true);
		publish.setReadWriteEnabled(true);
		publish.setIcalUrl("http://somedomain.com");
		SheetPublish newPublish = sheetResource.updatePublishStatus(1234L, publish);

		assertNull(newPublish.getIcalUrl());
		assertNotNull(newPublish.getReadOnlyFullUrl());
		assertNotNull(newPublish.getReadOnlyLiteUrl());
		assertNotNull(newPublish.getReadWriteUrl());
		
	}
}
