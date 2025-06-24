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
import mb.minecraft.model.Villager;


@RunWith(MockitoJUnitRunner.class)
public class VillagerDaoDummyImplTest {

	private static final Logger logger = LogManager.getLogger( VillagerDaoDummyImplTest.class );

	@InjectMocks
	VillagerDaoDummyImpl villagerDao;


	@Test
	public void testSelectOneById() {
		Villager villager = villagerDao.selectOneById( 699 );
		assertNotNull( villager );
		assertEquals( 699, villager.getId().intValue() );
		assertEquals( "Liam Z", villager.getName() );
		assertTrue( villager.isTagged() );
		assertEquals( 201, villager.getVillageId().intValue() );
		assertEquals( 105, villager.getTypeId().intValue() );
	}

	@Test
	public void testSelectOneByName() {
		Villager villager = villagerDao.selectOneByName( "Malcolm" );
		assertNotNull( villager );
		assertEquals( 702, villager.getId().intValue() );
		assertEquals( "Malcolm", villager.getName() );
		assertFalse( villager.isTagged() );
		assertEquals( 215, villager.getVillageId().intValue() );
		assertEquals( 104, villager.getTypeId().intValue() );
	}

	@Test
	public void testSelectAll() {
		List<Villager> villagers = villagerDao.selectAll();
		assertNotNull( villagers );
		assertEquals( 6, villagers.size() );

		Villager v1 = villagers.stream()
				.filter( v -> v.getName().equals( "Amy" ) )
				.findFirst()
				.get();
		assertEquals( 705, v1.getId().intValue() );

		Villager v2 = villagers.stream()
				.filter( v -> v.getName().equals( "Gary" ) )
				.findFirst()
				.get();
		assertEquals( 701, v2.getId().intValue() );
	}

	@Test
	public void testSelectAllForVillage() {
		Village calgary = Village.builder()
				.id( 213 )
				.name( "Calgary" )
				.build();
		List<Villager> villagers = villagerDao.selectAll( calgary );
		assertNotNull( villagers );
		assertEquals( 1, villagers.size() );

		Villager v1 = villagers.get(0);
		assertEquals( "Tyler", v1.getName() );
		assertEquals( 73, v1.getId().intValue() );
	}

	@Test
	public void testSelectAllForEmptyVillage() {
		Village empty = new Village();
		List<Villager> villagers = villagerDao.selectAll( empty );
		assertNotNull( villagers );
		assertEquals( 0, villagers.size() );
	}

	@Test
	public void testSelectAllForNullVillage() {
		List<Villager> villagers = villagerDao.selectAll( null );
		assertNotNull( villagers );
		assertEquals( 0, villagers.size() );
	}

	@Test
	public void testInsertOneSuccess() {
		Villager newVillager = Villager.builder()
				.id( 10 )
				.name( "Mikebro" )
				.build();
		Villager villager = villagerDao.insertOne( newVillager );
		assertNotNull( villager );
		assertEquals( 10, villager.getId().intValue() );
		assertEquals( "Mikebro", villager.getName() );
	}

	@Test
	public void testInsertOneIdFail() {
		Villager newVillager = Villager.builder()
				.id( 699 )
				.name( "Brock Samson" )
				.build();
		Exception e = assertThrows( DaoConstraintException.class, () -> villagerDao.insertOne( newVillager ) );
		assertNotNull( e );
		assertEquals( "Dataset of type \"Villager\" does not allow duplicate values for field \"ID\"", e.getMessage() );
		logger.error( e.getMessage() );
	}

	@Test
	public void testInsertOneNameFail() {
		Villager newVillager = Villager.builder()
				.id( 5 )
				.name( "Tyler" )
				.build();
		Exception e = assertThrows( DaoConstraintException.class, () -> villagerDao.insertOne( newVillager ) );
		assertNotNull( e );
		assertEquals( "Dataset of type \"Villager\" does not allow duplicate values for field \"NAME\"", e.getMessage() );
		logger.error( e.getMessage() );
	}

	@Test
	public void testInsertOneSpecifiedId() {
		Villager newVillager = Villager.builder()
				.id( 90000 )
				.name( "Johnny" )
				.build();
		Villager villager = villagerDao.insertOne( newVillager );
		assertNotNull( villager );
		assertEquals( 90000, villager.getId().intValue() );
		assertEquals( "Johnny", villager.getName() );
	}

	@Test
	public void testInsertOneDerivedId() {
		Villager newVillager = Villager.builder()
				.name( "Sean" )
				.build();
		Villager villager = villagerDao.insertOne( newVillager );
		assertNotNull( villager );
		assertNotNull( villager.getId() );
		assertTrue( villager.getId().intValue() > 0 );
	}

	@Test
	public void testUpdateExisting() {
		Villager villager = villagerDao.selectOneByName( "Amy" );
		assertNotNull( villager );
		int villagerId = villager.getId();

		int newVillageId = 201;
		villager.setVillageId( newVillageId );
		villagerDao.update( villager );

		Villager finalVillager = villagerDao.selectOneById( villagerId );
		assertNotNull( finalVillager );
		assertEquals( newVillageId, finalVillager.getVillageId().intValue() );
	}

	@Test
	public void testUpdateFailNonExisting() {
		Villager villager = Villager.builder()
				.id( 1 )
				.name( "Red Death" )
				.build();
		Villager response = villagerDao.update( villager );
		assertNull( response );

		Villager finalVillager = villagerDao.selectOneById( 1 );
		assertNull( finalVillager );
	}

	@Test
	public void testDeleteFail() {
		Villager deleteVillager = Villager.builder()
				.id( 1 )
				.name( "The Monarch" )
				.build();
		boolean wasDeleted = villagerDao.deleteOne( deleteVillager );
		assertFalse( wasDeleted );
	}

	@Test
	public void testDeleteSuccess() {
		int count = villagerDao.selectAll().size();

		Villager villager = villagerDao.selectOneByName( "Gary" );
		assertNotNull( villager );

		boolean wasDeleted = villagerDao.deleteOne( villager );
		assertTrue( wasDeleted );
		assertEquals( count - 1, villagerDao.selectAll().size() );
	}

}
