/**
 * Copyright (c) 2009 i>clicker (R) <http://www.iclicker.com/dnn/>
 *
 * This file is part of i>clicker Sakai integrate.
 *
 * i>clicker Sakai integrate is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * i>clicker Sakai integrate is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with i>clicker Sakai integrate.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.sakaiproject.iclicker.logic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;
import org.sakaiproject.iclicker.dao.IClickerDao;
import org.sakaiproject.iclicker.logic.stubs.ExternalLogicStub;
import org.sakaiproject.iclicker.model.ClickerRegistration;
import org.springframework.test.AbstractTransactionalSpringContextTests;

/**
 * Testing the Logic implementation methods
 * 
 * @author Aaron Zeckoski (azeckoski @ gmail.com)
 */
public class IClickerLogicImplTest extends AbstractTransactionalSpringContextTests {

    protected IClickerLogic logicImpl;

    private FakeDataPreload tdp;
    private ExternalLogicStub externalLogic;

    protected String[] getConfigLocations() {
        // point to the needed spring config files, must be on the classpath
        // (add component/src/webapp/WEB-INF to the build path in Eclipse),
        // they also need to be referenced in the project.xml file
        return new String[] { "hibernate-test.xml", "spring-hibernate.xml" };
    }

    // run this before each test starts
    protected void onSetUpBeforeTransaction() throws Exception {
    }

    // run this before each test starts and as part of the transaction
    protected void onSetUpInTransaction() {
        // load the spring created dao class bean from the Spring Application Context
        IClickerDao dao = (IClickerDao) applicationContext
                .getBean("org.sakaiproject.iclicker.dao.IClickerDao");
        if (dao == null) {
            throw new NullPointerException("DAO could not be retrieved from spring context");
        }

        // load up the test data preloader from spring
        tdp = (FakeDataPreload) applicationContext
                .getBean("org.sakaiproject.iclicker.logic.test.FakeDataPreload");
        if (tdp == null) {
            throw new NullPointerException(
                    "FakeDataPreload could not be retrieved from spring context");
        }

        // reload the test objects in this session
        tdp.reloadTestData();

        // init the class if needed

        // setup the mock objects
        externalLogic = new ExternalLogicStub();

        // create and setup the object to be tested
        logicImpl = new IClickerLogic();
        logicImpl.setDao(dao);
        logicImpl.setExternalLogic(externalLogic);

        // can set up the default mock object returns here if desired
        // Note: Still need to activate them in the test methods though

        // run the init
        logicImpl.init();
    }

    public void testGetItemById() {
        ClickerRegistration item = logicImpl.getItemById(tdp.item1.getId());
        assertNotNull(item);
        assertEquals(item, tdp.item1);

        ClickerRegistration baditem = logicImpl.getItemById((long) -1);
        assertNull(baditem);

        try {
            logicImpl.getItemById(null);
            fail("Should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            assertNotNull(e.getMessage());
        }
    }

    public void testCanWriteItem() {
        // users can only write their own stuff (unless admin who can write anything
        // testing perms as a normal user
        assertFalse(logicImpl.canWriteItem(tdp.adminitem, FakeDataPreload.USER_ID));
        assertFalse(logicImpl.canWriteItem(tdp.maintitem, FakeDataPreload.USER_ID));
        assertTrue(logicImpl.canWriteItem(tdp.item1, FakeDataPreload.USER_ID));
        assertTrue(logicImpl.canWriteItem(tdp.item2, FakeDataPreload.USER_ID));
        assertTrue(logicImpl.canWriteItem(tdp.item3, FakeDataPreload.USER_ID));

        // testing perms as admin user
        assertTrue(logicImpl.canWriteItem(tdp.adminitem, FakeDataPreload.ADMIN_USER_ID));
        assertTrue(logicImpl.canWriteItem(tdp.maintitem, FakeDataPreload.ADMIN_USER_ID));
        assertTrue(logicImpl.canWriteItem(tdp.item1, FakeDataPreload.ADMIN_USER_ID));
    }

    /* ignoring this test for now
    public void ignoretestGetAllVisibleItems() {

        // add 2 items to test if we can see the visible one and not the hidden one
        ClickerRegistration itemMaint = new ClickerRegistration("MMMM2222",
                FakeDataPreload.MAINT_USER_ID);
        logicImpl.saveItem(itemMaint);

        List<ClickerRegistration> l = logicImpl.getAllVisibleItems(FakeDataPreload.USER_ID,
                FakeDataPreload.LOCATION1_ID); // test normal user
        assertNotNull(l);
        assertEquals(3, l.size());
        assertTrue(l.contains(tdp.item1));
        assertTrue(l.contains(tdp.item2));
        assertTrue(l.contains(tdp.item3));
        assertTrue(!l.contains(tdp.accessitem));
        assertTrue(!l.contains(tdp.maintitem));
        assertTrue(!l.contains(tdp.adminitem));
        assertTrue(!l.contains(itemMaint));

        List<ClickerRegistration> laccess = logicImpl.getAllVisibleItems(
                FakeDataPreload.USER_ID, FakeDataPreload.LOCATION1_ID); // test normal user
        assertNotNull(laccess);
        assertEquals(3, laccess.size());
        assertTrue(laccess.contains(tdp.item1));
        assertTrue(laccess.contains(tdp.item2));
        assertTrue(laccess.contains(tdp.item3));
        assertTrue(!laccess.contains(tdp.accessitem));
        assertTrue(!laccess.contains(tdp.maintitem));
        assertTrue(!laccess.contains(tdp.adminitem));
        assertTrue(!laccess.contains(itemMaint));

        List<ClickerRegistration> lmaintain = logicImpl.getAllVisibleItems(
                FakeDataPreload.MAINT_USER_ID, FakeDataPreload.LOCATION1_ID); // test maintainer
        assertNotNull(lmaintain);
        assertEquals(2, lmaintain.size());
        assertTrue(!lmaintain.contains(tdp.item1));
        assertTrue(!lmaintain.contains(tdp.item2));
        assertTrue(!lmaintain.contains(tdp.item3));
        assertTrue(!lmaintain.contains(tdp.accessitem));
        assertTrue(lmaintain.contains(tdp.maintitem));
        assertTrue(!lmaintain.contains(tdp.adminitem));
        assertTrue(lmaintain.contains(itemMaint));

        List<ClickerRegistration> ladmin = logicImpl.getAllVisibleItems(
                FakeDataPreload.ADMIN_USER_ID, FakeDataPreload.LOCATION1_ID); // test admin
        assertNotNull(ladmin);
        assertEquals(5, ladmin.size());
        assertTrue(ladmin.contains(tdp.item1));
        assertTrue(!ladmin.contains(tdp.item2));
        assertTrue(ladmin.contains(itemMaint));
    }
    */

    public void testRemoveItem() {
        try {
            logicImpl.removeItem(tdp.adminitem); // user cannot delete this
            fail("Should have thrown SecurityException");
        } catch (SecurityException e) {
            assertNotNull(e.getMessage());
        }

        try {
            logicImpl.removeItem(tdp.adminitem); // permed user cannot delete this
            fail("Should have thrown SecurityException");
        } catch (SecurityException e) {
            assertNotNull(e.getMessage());
        }

        try {
            // normal user cannot remove
            logicImpl.removeItem(tdp.item1);
            fail("Should have thrown SecurityException");
        } catch (SecurityException e) {
            assertNotNull(e.getMessage());
        }

        externalLogic.currentUserId = FakeDataPreload.ADMIN_USER_ID;
        logicImpl.removeItem(tdp.item1); // admin user can delete this
        ClickerRegistration item = logicImpl.getItemById(tdp.item1.getId());
        assertNull(item);
    }

    public void testSaveItem() {

        ClickerRegistration item = new ClickerRegistration("AA11AA11", FakeDataPreload.USER_ID);
        logicImpl.saveItem(item);
        Long itemId = item.getId();
        assertNotNull(itemId);

        // test saving an incomplete item
        ClickerRegistration incompleteItem = new ClickerRegistration();
        incompleteItem.setClickerId("AA11AA11");

        logicImpl.saveItem(incompleteItem);
        assertNotNull(incompleteItem.getId());
        assertNotNull(incompleteItem.getOwnerId());

        // test saving with invalid clicker ID
        try {
            ClickerRegistration invalid = new ClickerRegistration("NNEEWW11", FakeDataPreload.USER_ID);
            logicImpl.saveItem(invalid);
            fail("Should have died");
        } catch (ClickerIdInvalidException e) {
            assertNotNull(e.getMessage());
        }

        Long incItemId = item.getId();
        assertNotNull(incItemId);

        item = logicImpl.getItemById(incItemId);
        assertNotNull(item);
        assertEquals(item.getOwnerId(), FakeDataPreload.USER_ID);
        assertEquals(item.getLocationId(), null);

        // test saving a null value for failure
        try {
            logicImpl.saveItem(null);
            fail("Should have thrown NullPointerException");
        } catch (IllegalArgumentException e) {
            assertNotNull(e.getMessage());
        }
    }
    public void testValidateClickerId() {
        try {
            logicImpl.validateClickerId(null);
            fail("should have thrown exception");
        } catch (ClickerIdInvalidException e) {
            assertNotNull(e.getMessage());
            assertEquals(ClickerIdInvalidException.Failure.EMPTY, e.failure);
        }

        try {
            logicImpl.validateClickerId("");
            fail("should have thrown exception");
        } catch (ClickerIdInvalidException e) {
            assertNotNull(e.getMessage());
            assertEquals(ClickerIdInvalidException.Failure.EMPTY, e.failure);
        }
        try {
            logicImpl.validateClickerId("totally invalid");
            fail("should have thrown exception");
        } catch (ClickerIdInvalidException e) {
            assertNotNull(e.getMessage());
            assertEquals(ClickerIdInvalidException.Failure.LENGTH, e.failure);
        }

        try {
            logicImpl.validateClickerId("invalid1");
            fail("should have thrown exception");
        } catch (ClickerIdInvalidException e) {
            assertNotNull(e.getMessage());
            assertEquals(ClickerIdInvalidException.Failure.CHARS, e.failure);
        }

        try {
            logicImpl.validateClickerId("AABB1122");
            fail("should have thrown exception");
        } catch (ClickerIdInvalidException e) {
            assertNotNull(e.getMessage());
            assertEquals(ClickerIdInvalidException.Failure.CHECKSUM, e.failure);
        }

        try {
            logicImpl.validateClickerId(IClickerLogic.CLICKERID_SAMPLE);
            fail("should have thrown exception");
        } catch (ClickerIdInvalidException e) {
            assertNotNull(e.getMessage());
            assertEquals(ClickerIdInvalidException.Failure.SAMPLE, e.failure);
        }

        // Test GO IDs
        try {
            logicImpl.validateClickerId("ABCDEF1234__");
            fail("should have thrown exception");
        } catch (ClickerIdInvalidException e) {
            assertNotNull(e.getMessage());
            assertEquals(ClickerIdInvalidException.Failure.GO_CHARS, e.failure);
        }

        try {
            logicImpl.validateClickerId("ABCDEF1234");
            fail("should have thrown exception");
        } catch (ClickerIdInvalidException e) {
            assertNotNull(e.getMessage());
            assertEquals(ClickerIdInvalidException.Failure.LENGTH, e.failure);
        }

        try {
            logicImpl.validateClickerId("ABCDEF12345678");
            fail("should have thrown exception");
        } catch (ClickerIdInvalidException e) {
            assertNotNull(e.getMessage());
            assertEquals(ClickerIdInvalidException.Failure.LENGTH, e.failure);
        }

        // make sure valid ones work
        try {
            String id = logicImpl.validateClickerId("1445e7b6");
            assertEquals("1445E7B6", id);
        } catch (ClickerIdInvalidException e) {
            fail("should not have failed: " + e);
        }

        try {
            String id = logicImpl.validateClickerId("1445E7B6");
            assertEquals("1445E7B6", id);
        } catch (ClickerIdInvalidException e) {
            fail("should not have failed: " + e);
        }
    }

    public void testTranslateClickerId() {
        String result;

        result = logicImpl.translateClickerId(null);
        assertEquals(null, result);

        result = logicImpl.translateClickerId("");
        assertEquals(null, result);

        result = logicImpl.translateClickerId(IClickerLogic.CLICKERID_SAMPLE);
        assertEquals(null, result);

        result = logicImpl.translateClickerId("11111111");
        assertEquals(null, result);

        result = logicImpl.translateClickerId("22222222");
        assertEquals("02222202", result);

        result = logicImpl.translateClickerId("33333333");
        assertEquals(null, result);

        result = logicImpl.translateClickerId("44444444");
        assertEquals("04444404", result);

        result = logicImpl.translateClickerId("55555555");
        assertEquals(null, result);

        result = logicImpl.translateClickerId("66666666");
        assertEquals(null, result);

        result = logicImpl.translateClickerId("77777777");
        assertEquals(null, result);

        result = logicImpl.translateClickerId("88888888");
        assertEquals("08888808", result);

        result = logicImpl.translateClickerId("99999999");
        assertEquals(null, result);
    }

    public void testEncodeClickerReg() {
        ClickerRegistration registration = new ClickerRegistration("11111111","azeckoski-123456");
        String xml = logicImpl.encodeClickerRegistration(registration);
        assertNotNull(xml);
    }

    public void testMakeClickerIdsAndDates() {
        ClickerRegistration registration = new ClickerRegistration("11111111","azeckoski-1");
        ClickerRegistration registration2 = new ClickerRegistration("22222222","becky-2");
        String[] result;

        List<ClickerRegistration> clickers = new ArrayList<ClickerRegistration>();
        clickers.add(registration);
        result = logicImpl.makeClickerIdsAndDates(clickers);
        assertNotNull(result);
        assertEquals("11111111", result[0]);

        clickers.clear();
        clickers.add(registration2);
        result = logicImpl.makeClickerIdsAndDates(clickers);
        assertNotNull(result);
        assertEquals("22222222,02222202", result[0]);

        clickers.add(registration);
        result = logicImpl.makeClickerIdsAndDates(clickers);
        assertNotNull(result);
        assertEquals("22222222,02222202,11111111", result[0]);
    }

    public void testDecodeGradebookXML() {
        // fully complete XML with 3 items and 3 students
        String xml = "<coursegradebook courseid='course-id-111'> " +
        		"<user id='student1' usertype='S'> <lineitem name='item 1' pointspossible='100.0' type='internal' score='93.0'/> <lineitem name='item 2' pointspossible='100' type='internal' score='87.0'/> <lineitem name='item 3' pointspossible='50.0' type='internal' score='47.0'/> </user> " +
        		"<user id='student2' usertype='S'> <lineitem name='item 1' pointspossible='100.0' type='internal' score='77.0'/> <lineitem name='item 2' pointspossible='100' type='internal' score='91.0'/> <lineitem name='item 3' pointspossible='50.0' type='internal' score='41.0'/> </user> " +
        		"<user id='student3' usertype='S'> <lineitem name='item 1' pointspossible='100.0' type='internal' score='57.0'/> <lineitem name='item 2' pointspossible='100' type='internal' score='63.0'/> <lineitem name='item 3' pointspossible='50.0' type='internal' score='33.0'/> </user> " +
        		"</coursegradebook>";
        Gradebook gb = logicImpl.decodeGradebookXML(xml);
        assertNotNull(gb);
        assertNotNull(gb.items);
        assertEquals(3, gb.items.size());
        assertNotNull(gb.items.get(0).scores);
        assertEquals(3, gb.items.get(0).scores.size());
        assertNotNull(gb.items.get(1).scores);
        assertEquals(3, gb.items.get(1).scores.size());
        assertNotNull(gb.items.get(2).scores);
        assertEquals(3, gb.items.get(2).scores.size());

        // try XML with varying completeness
        xml = "<coursegradebook courseid='course-id-111'> <user id='student1' usertype='S'> <lineitem name='item 1' pointspossible='100.0' type='internal' score='93.0'/> <lineitem name='item 2' pointspossible='100' type='internal' score='87.0'/> <lineitem name='item 3' pointspossible='50.0' type='internal' score='47.0'/> </user> <user id='student2' usertype='S'> <lineitem name='item 2' pointspossible='100' type='internal' score='91.0'/> <lineitem name='item 3' pointspossible='50.0' type='internal' score='41.0'/> </user> <user id='student3' usertype='S'> <lineitem name='item 3' pointspossible='50.0' type='internal' score='33.0'/> </user> <user id='student4' usertype='S'> </user> <user id='inst3' usertype='I'> </user> </coursegradebook>";
        gb = logicImpl.decodeGradebookXML(xml);
        assertNotNull(gb);
        assertNotNull(gb.items);
        assertEquals(3, gb.items.size());
        assertNotNull(gb.items.get(0).scores);
        assertEquals(1, gb.items.get(0).scores.size());
        assertNotNull(gb.items.get(1).scores);
        assertEquals(2, gb.items.get(1).scores.size());
        assertNotNull(gb.items.get(2).scores);
        assertEquals(3, gb.items.get(2).scores.size());

        // try XML with varying completeness
        xml = "<coursegradebook courseid='course-id-111'> " +
        		"<user id='student1' usertype='S'> <lineitem name='item 1' pointspossible='100.0' type='internal' score='93.0'/> <lineitem name='item 2' pointspossible='100' type='internal' score='87.0'/> <lineitem name='item 3' pointspossible='50.0' type='internal' score='47.0'/> </user> " +
        		"<user id='student2' usertype='S'> <lineitem name='item 2' pointspossible='100' type='internal' score='91.0'/> <lineitem name='item 3' pointspossible='50.0' type='internal' score='41.0'/> </user> </coursegradebook>";
        gb = logicImpl.decodeGradebookXML(xml);
        assertNotNull(gb);
        assertNotNull(gb.items);
        assertEquals(3, gb.items.size());
        assertNotNull(gb.items.get(0).scores);
        assertEquals(1, gb.items.get(0).scores.size());
        assertNotNull(gb.items.get(1).scores);
        assertEquals(2, gb.items.get(1).scores.size());
        assertNotNull(gb.items.get(2).scores);
        assertEquals(2, gb.items.get(2).scores.size());

        // mixed order
        xml = "<coursegradebook courseid='course-id-111'> " +
        		"<user id='student1' usertype='S'> <lineitem name='item 1' pointspossible='100.0' type='internal' score='93.0'/> <lineitem name='item 2' pointspossible='100' type='internal' score='87.0'/> </user> " +
        		"<user id='student2' usertype='S'> <lineitem name='item 2' pointspossible='100.0' type='internal' score='77.0'/> <lineitem name='item 3' pointspossible='100' type='internal' score='91.0'/> </user> " +
        		"<user id='student3' usertype='S'> <lineitem name='item 3' pointspossible='100.0' type='internal' score='57.0'/> <lineitem name='item 4' pointspossible='100' type='internal' score='63.0'/> </user> " +
        		"<user id='student4' usertype='S'> <lineitem name='item 2' pointspossible='100.0' type='internal' score='100.0'/> <lineitem name='item 1' pointspossible='100' type='internal' score='100.0'/> </user> " +
        		"</coursegradebook>";
        gb = logicImpl.decodeGradebookXML(xml);
        assertNotNull(gb);
        assertNotNull(gb.items);
        assertEquals(4, gb.items.size());
        assertNotNull(gb.items.get(0).scores);
        assertEquals(2, gb.items.get(0).scores.size());
        assertNotNull(gb.items.get(1).scores);
        assertEquals(3, gb.items.get(1).scores.size());
        assertNotNull(gb.items.get(2).scores);
        assertEquals(2, gb.items.get(2).scores.size());
        assertNotNull(gb.items.get(3).scores);
        assertEquals(1, gb.items.get(3).scores.size());

        try {
            // no courseid
            xml = "<coursegradebook > " +
            		"<user id='student1' usertype='S'> <lineitem name='item 2' pointspossible='100' type='internal' score='87.0'/> <lineitem name='item 3' pointspossible='50.0' type='internal' score='47.0'/> </user> " +
            		"</coursegradebook>";
            //noinspection UnusedAssignment,UnusedAssignment
            gb = logicImpl.decodeGradebookXML(xml);
            fail("Should have thrown NullPointerException");
        } catch (IllegalArgumentException e) {
            assertNotNull(e.getMessage());
        }

        // test saving a null value for failure
        try {
            logicImpl.decodeGradebookXML(null);
            fail("Should have thrown NullPointerException");
        } catch (IllegalArgumentException e) {
            assertNotNull(e.getMessage());
        }
    }

    public void testEncodeUploadResults() {
        String xml;
        String courseId = "course-id-1111";
        String gbId = "gb-id-11111";
        String userId = "user-id-AZ";
        List<GradebookItem> items = new ArrayList<GradebookItem>();

        GradebookItem item0 = new GradebookItem(gbId, "item0");
        item0.scores = new ArrayList<GradebookItemScore>();
        item0.scoreErrors = new HashMap<String, String>();
        GradebookItemScore item0_1 = new GradebookItemScore(item0.name, userId, "50");
        item0_1.id = "0_1";
        item0.scores.add( item0_1 );
        GradebookItemScore item0_2 = new GradebookItemScore(item0.name, userId+"2", "60");
        item0_2.id = "0_2";
        item0.scores.add( item0_2 );
        items.add( item0 );

        // valid one first
        xml = logicImpl.encodeSaveGradebookResults(courseId, items);
        assertNull(xml);

        // now one with lots of errors
        GradebookItem item1 = new GradebookItem(gbId, "item1");
        item1.scores = new ArrayList<GradebookItemScore>();
        item1.scoreErrors = new HashMap<String, String>();
        GradebookItemScore item1_1 = new GradebookItemScore(item1.name, userId, "50");
        item1_1.id = "1_1";
        item1_1.error = AbstractExternalLogic.USER_DOES_NOT_EXIST_ERROR;
        item1.scoreErrors.put(item1_1.id, AbstractExternalLogic.USER_DOES_NOT_EXIST_ERROR);
        item1.scores.add( item1_1 );
        items.add( item1 );

        GradebookItem item2 = new GradebookItem(gbId, "item2", 50.0d, null, "type", true);
        item2.scores = new ArrayList<GradebookItemScore>();
        item2.scoreErrors = new HashMap<String, String>();
        GradebookItemScore item2_1 = new GradebookItemScore(item2.name, userId, "40");
        item2_1.id = "2_1";
        item2_1.error = AbstractExternalLogic.SCORE_UPDATE_ERRORS;
        item2.scoreErrors.put(item2_1.id, AbstractExternalLogic.SCORE_UPDATE_ERRORS);
        item2.scores.add( item2_1 );
        items.add( item2 );

        GradebookItem item3 = new GradebookItem(gbId, "item3");
        item3.scores = new ArrayList<GradebookItemScore>();
        item3.scoreErrors = new HashMap<String, String>();
        GradebookItemScore item3_1 = new GradebookItemScore(item3.name, userId, "30");
        item3_1.id = "3_1";
        item3_1.error = AbstractExternalLogic.POINTS_POSSIBLE_UPDATE_ERRORS;
        item3.scoreErrors.put(item3_1.id, AbstractExternalLogic.POINTS_POSSIBLE_UPDATE_ERRORS);
        item3.scores.add( item3_1 );
        GradebookItemScore item3_2 = new GradebookItemScore(item3.name, userId, "20");
        item3_2.id = "3_2";
        item3_2.error = "RANDOM ERROR XXX";
        item3.scoreErrors.put(item3_2.id, "RANDOM ERROR XXX");
        item3.scores.add( item3_2 );
        items.add( item3 );

        xml = logicImpl.encodeSaveGradebookResults(courseId, items);
        assertNotNull(xml);
        assertTrue(xml.indexOf(userId) > 0);
        assertTrue(xml.indexOf(item2.name) > 0);
        assertTrue(xml.indexOf(item3.name) > 0);

        // null failure
        try {
            logicImpl.encodeSaveGradebookResults(courseId, null);
            fail("Should have thrown exception");
        } catch (IllegalArgumentException e) {
            assertNotNull(e.getMessage());
        }

        try {
            logicImpl.encodeSaveGradebookResults(null, items);
            fail("Should have thrown exception");
        } catch (IllegalArgumentException e) {
            assertNotNull(e.getMessage());
        }
    }

    /**
     * How to create a valid encoded key:
     * Take the input key (must be 10 chars long or longer) and append ':' and the current unix timestamp in seconds
     * Take that string and SHA-1 encode it into a hexadecimal encoded string
     * Take the hex string and append '|' and the same timestamp as before
     * This is the encoded key which should be sent with the request
     * 
     * NOTE: it is safe to pass the encode key in the clear (as a url param or otherwise) 
     * as it is one way encrypted and very very difficult to brute force decrypt
     * 
     * Sample key:
     * abcdef1234566890
     * Sample timestamp:
     * 1332470760
     * Encoded key:
     * cc80462bfc0da7e614237d7cab4b7971b0e71e9f|1332470760
     */
    public void testVerifyKey() {
        String key = "abcdef1234566890";
        logicImpl.setSharedKey(key);

        // test expired timestamp
        String encodedKey = "cc80462bfc0da7e614237d7cab4b7971b0e71e9f|1332470760";
        try {
            logicImpl.verifyKey(encodedKey);
            fail("should have died");
        } catch (SecurityException e) {
            assertNotNull(e.getMessage());
        }

        // test invalid format
        try {
            logicImpl.verifyKey("xxxxxxxxxxxxx");
            fail("should have died");
        } catch (IllegalArgumentException e) {
            assertNotNull(e.getMessage());
        }
        try {
            logicImpl.verifyKey("xxxxxxxxxxxxx|");
            fail("should have died");
        } catch (IllegalArgumentException e) {
            assertNotNull(e.getMessage());
        }
        try {
            logicImpl.verifyKey("xxxxxxxx|12344ffff");
            fail("should have died");
        } catch (IllegalArgumentException e) {
            assertNotNull(e.getMessage());
        }

        // test valid encoded key
        long timestamp = System.currentTimeMillis() / 1000l;
        byte[] sha1Bytes = DigestUtils.sha(key + ":" + timestamp);
        encodedKey = Hex.encodeHexString(sha1Bytes) + "|" + timestamp;
        boolean result = logicImpl.verifyKey(encodedKey);
        assertTrue(result);
        //System.out.println("key: "+key+" , encoded: "+encodedKey);

        // for testing other keys
        /*
        key = "66f3b80a-96b5-41c0-a2fb-1d0b17aec523";
        logicImpl.setSharedKey(key);
        //timestamp = System.currentTimeMillis() / 1000l;
        timestamp = 1333495162;
        sha1Bytes = DigestUtils.sha(key + ":" + timestamp);
        encodedKey = Hex.encodeHexString(sha1Bytes) + "|" + timestamp;
        result = logicImpl.verifyKey(encodedKey);
        System.out.println("key: "+key+", timestamp: "+timestamp+", encoded: "+encodedKey+", result="+result);
        assertTrue(result);
        */
    }

}
