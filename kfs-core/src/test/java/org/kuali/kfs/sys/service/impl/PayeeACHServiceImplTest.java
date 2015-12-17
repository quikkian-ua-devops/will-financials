package org.kuali.kfs.sys.service.impl;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.junit.BeforeClass;
import org.junit.Test;
import org.kuali.kfs.krad.bo.PersistableBusinessObject;
import org.kuali.kfs.krad.service.BusinessObjectService;
import org.kuali.kfs.pdp.PdpPropertyConstants;
import org.kuali.kfs.pdp.businessobject.PayeeACHAccount;
import org.kuali.rice.krad.bo.BusinessObject;

public class PayeeACHServiceImplTest {

	private static PayeeACHServiceImpl payeeACHServiceImpl;

	@BeforeClass
	public static void setUp() throws Exception {
		payeeACHServiceImpl = new PayeeACHServiceImpl();
		payeeACHServiceImpl
				.setBusinessObjectService(new StubBusinessObjectService());
	}

	public void tearDown() throws Exception {
	}

	@Test
	public void noPayeeExists() throws Exception {
		assertFalse(payeeACHServiceImpl.isPayeeSignedUpForACH("test", "0987654321"));
	}

	@Test
	public void payeeExist() throws Exception {
		assertTrue(payeeACHServiceImpl.isPayeeSignedUpForACH("test", "1234567890"));
	}
	
	@Test
	public void payeeIsNull() throws Exception {
		assertFalse(payeeACHServiceImpl.isPayeeSignedUpForACH(null, null));
	}

	protected static class StubBusinessObjectService implements BusinessObjectService {

		@Override
		public <T extends PersistableBusinessObject> T save(T bo) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public List<? extends PersistableBusinessObject> save(
				List<? extends PersistableBusinessObject> businessObjects) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public PersistableBusinessObject linkAndSave(
				PersistableBusinessObject bo) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public List<? extends PersistableBusinessObject> linkAndSave(
				List<? extends PersistableBusinessObject> businessObjects) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public <T extends BusinessObject> T findBySinglePrimaryKey(
				Class<T> clazz, Object primaryKey) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public <T extends BusinessObject> T findByPrimaryKey(Class<T> clazz,
				Map<String, ?> primaryKeys) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public PersistableBusinessObject retrieve(
				PersistableBusinessObject object) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public <T extends BusinessObject> Collection<T> findAll(Class<T> clazz) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public <T extends BusinessObject> Collection<T> findAllOrderBy(
				Class<T> clazz, String sortField, boolean sortAscending) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public <T extends BusinessObject> Collection<T> findMatching(
				Class<T> clazz, Map<String, ?> fieldValues) {

			if (fieldValues.get(PdpPropertyConstants.PAYEE_ID_NUMBER) == null)
				return null;
			else if (((String) fieldValues.get(PdpPropertyConstants.PAYEE_ID_NUMBER)).equalsIgnoreCase("1234567890")) {
				List list = new ArrayList();
				list.add(new PayeeACHAccount());
				return list;
			}
			
			// looks up send empty lists if nothing found.
			return new ArrayList<T>();
		}

		@Override
		public int countMatching(Class clazz, Map<String, ?> fieldValues) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public int countMatching(Class clazz,
				Map<String, ?> positiveFieldValues,
				Map<String, ?> negativeFieldValues) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public <T extends BusinessObject> Collection<T> findMatchingOrderBy(
				Class<T> clazz, Map<String, ?> fieldValues, String sortField,
				boolean sortAscending) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public void delete(PersistableBusinessObject bo) {
			// TODO Auto-generated method stub

		}

		@Override
		public void delete(List<? extends PersistableBusinessObject> boList) {
			// TODO Auto-generated method stub

		}

		@Override
		public void deleteMatching(Class clazz, Map<String, ?> fieldValues) {
			// TODO Auto-generated method stub

		}

		@Override
		public BusinessObject getReferenceIfExists(BusinessObject bo,
				String referenceName) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public void linkUserFields(PersistableBusinessObject bo) {
			// TODO Auto-generated method stub

		}

		@Override
		public void linkUserFields(List<PersistableBusinessObject> bos) {
			// TODO Auto-generated method stub

		}

		@Override
		public PersistableBusinessObject manageReadOnly(
				PersistableBusinessObject bo) {
			// TODO Auto-generated method stub
			return null;
		}

	}

}
