package mb.minecraft.dao.impl.dummy;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;

import mb.minecraft.dao.DaoConstraintException;
import mb.minecraft.model.Village;


@RunWith(MockitoJUnitRunner.class)
public class VillageDaoDummyImplTest {

	private static final Logger logger = LogManager.getLogger( VillageDaoDummyImplTest.class );

	@InjectMocks
	VillageDaoDummyImpl villageDao;


	@Test
	public void testSelectOneById() {
		Village village = villageDao.selectOneById( 201 );
		assertNotNull( village );
		assertEquals( 201, village.getId().intValue() );
		assertEquals( "Deep Water Cove", village.getName() );
	}

	@Test
	public void testSelectOneByName() {
		Village village = villageDao.selectOneByName( "Heart" );
		assertNotNull( village );
		assertEquals( 202, village.getId().intValue() );
		assertEquals( "Heart", village.getName() );
	}

	@Test
	public void testSelectAll() {
		List<Village> villages = villageDao.selectAll();
		assertNotNull( villages );
		assertEquals( 10, villages.size() );

		Village v1 = villages.stream()
				.filter( v -> v.getName().equals( "Geelong" ) )
				.findFirst()
				.get();
		assertEquals( 212, v1.getId().intValue() );
		assertEquals( "Geelong", v1.getName() );

		Village v2 = villages.stream()
				.filter( v -> v.getName().equals( "Newark" ) )
				.findFirst()
				.get();
		assertEquals( 215, v2.getId().intValue() );
		assertEquals( "Newark", v2.getName() );
	}

	@Test
	public void testInsertOneSuccess() {
		Village newVillage = Village.builder()
				.id( 10 )
				.name( "Pacifica" )
				.build();
		Village village = villageDao.insertOne( newVillage );
		assertNotNull( village );
		assertEquals( 10, village.getId().intValue() );
		assertEquals( "Pacifica", village.getName() );
	}

	@Test
	public void testInsertOneIdFail() {
		Village newVillage = Village.builder()
				.id( 212 )
				.name( "Duplicate-Id-Fail Village" )
				.build();
		Exception e = assertThrows( DaoConstraintException.class, () -> villageDao.insertOne( newVillage ) );
		assertNotNull( e );
		assertEquals( "Dataset of type \"Village\" does not allow duplicate values for field \"ID\"", e.getMessage() );
		logger.error( e.getMessage() );
	}

	@Test
	public void testInsertOneNameFail() {
		Village newVillage = Village.builder()
				.id( 5 )
				.name( "Deep Water Cove" )
				.build();
		Exception e = assertThrows( DaoConstraintException.class, () -> villageDao.insertOne( newVillage ) );
		assertNotNull( e );
		assertEquals( "Dataset of type \"Village\" does not allow duplicate values for field \"NAME\"", e.getMessage() );
		logger.error( e.getMessage() );
	}

	@Test
	public void testInsertOneSpecifiedId() {
		Village newVillage = Village.builder()
				.id( 90000 )
				.name( "Pacifica" )
				.build();
		Village village = villageDao.insertOne( newVillage );
		assertNotNull( village );
		assertEquals( 90000, village.getId().intValue() );
		assertEquals( "Pacifica", village.getName() );
	}

	@Test
	public void testInsertOneDerivedId() {
		Village newVillage = Village.builder()
				.name( "Northern Outpost" )
				.build();
		Village village = villageDao.insertOne( newVillage );
		assertNotNull( village );
		assertNotNull( village.getId() );
		assertTrue( village.getId().intValue() > 0 );
	}

	@Test
	public void testUpdateExisting() {
		Village village = villageDao.selectOneByName( "Heart" );
		assertNotNull( village );
		int villageId = village.getId();

		String newName = "Heart of the Desert";
		village.setName( newName );
		villageDao.update( village );

		Village finalVillage = villageDao.selectOneById( villageId );
		assertNotNull( finalVillage );
		assertEquals( newName, finalVillage.getName() );
	}

	@Test
	public void testUpdateFailNonExisting() {
		Village village = Village.builder()
				.id( 1 )
				.name( "The Igloo" )
				.build();
		Village response = villageDao.update( village );
		assertNull( response );

		Village finalVillage = villageDao.selectOneById( 1 );
		assertNull( finalVillage );
	}

	@Test
	public void testDeleteFail() {
		Village deleteVillage = Village.builder()
				.id( 1 )
				.name( "Earth" )
				.build();
		boolean wasDeleted = villageDao.deleteOne( deleteVillage );
		assertFalse( wasDeleted );
	}

	@Test
	public void testDeleteSuccess() {
		int count = villageDao.selectAll().size();

		Village village = villageDao.selectOneByName( "Newark" );
		assertNotNull( village );

		boolean wasDeleted = villageDao.deleteOne( village );
		assertTrue( wasDeleted );
		assertEquals( count - 1, villageDao.selectAll().size() );
	}

}
