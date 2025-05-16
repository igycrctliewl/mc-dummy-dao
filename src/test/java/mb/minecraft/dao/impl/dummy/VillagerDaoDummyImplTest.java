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
import mb.minecraft.model.Villager;


@RunWith(MockitoJUnitRunner.class)
public class VillagerDaoDummyImplTest {

	private static final Logger logger = LogManager.getLogger( VillagerDaoDummyImplTest.class );

	@InjectMocks
	VillagerDaoDummyImpl villagerDao;


	@Test
	public void testSelectOneById() {
		Villager villager = villagerDao.selectOneById( 699L );
		assertNotNull( villager );
		assertEquals( 699L, villager.getId().longValue() );
		assertEquals( "Liam Z", villager.getName() );
		assertTrue( villager.isTagged() );
		assertEquals( 201L, villager.getVillageId().longValue() );
		assertEquals( 105L, villager.getTypeId().longValue() );
	}

	@Test
	public void testSelectOneByName() {
		Villager villager = villagerDao.selectOneByName( "Malcolm" );
		assertNotNull( villager );
		assertEquals( 702L, villager.getId().longValue() );
		assertEquals( "Malcolm", villager.getName() );
		assertFalse( villager.isTagged() );
		assertEquals( 215L, villager.getVillageId().longValue() );
		assertNull( villager.getTypeId() );
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
		assertEquals( 705L, v1.getId().longValue() );

		Villager v2 = villagers.stream()
				.filter( v -> v.getName().equals( "Gary" ) )
				.findFirst()
				.get();
		assertEquals( 701L, v2.getId().longValue() );
	}

	@Test
	public void testInsertOneSuccess() {
		Villager newVillager = Villager.builder()
				.id( 10L )
				.name( "Mikebro" )
				.build();
		Villager villager = villagerDao.insertOne( newVillager );
		assertNotNull( villager );
		assertEquals( 10L, villager.getId().longValue() );
		assertEquals( "Mikebro", villager.getName() );
	}

	@Test
	public void testInsertOneIdFail() {
		Villager newVillager = Villager.builder()
				.id( 699L )
				.name( "Brock Samson" )
				.build();
		Exception e = assertThrows( DaoConstraintException.class, () -> villagerDao.insertOne( newVillager ) );
		assertNotNull( e );
		assertEquals( "Dataset of type \"Villager\" does not allow duplicate values for field \"ID\"", e.getMessage() );
	}

	@Test
	public void testInsertOneNameFail() {
		Villager newVillager = Villager.builder()
				.id( 5L )
				.name( "Tyler" )
				.build();
		Exception e = assertThrows( DaoConstraintException.class, () -> villagerDao.insertOne( newVillager ) );
		assertNotNull( e );
		assertEquals( "Dataset of type \"Villager\" does not allow duplicate values for field \"NAME\"", e.getMessage() );
	}

	@Test
	public void testInsertOneSpecifiedId() {
		Villager newVillager = Villager.builder()
				.id( 90000L )
				.name( "Johnny" )
				.build();
		Villager villager = villagerDao.insertOne( newVillager );
		assertNotNull( villager );
		assertEquals( 90000L, villager.getId().longValue() );
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
		assertTrue( villager.getId().longValue() > 0 );
	}

	@Test
	public void testUpdateExisting() {
		Villager villager = villagerDao.selectOneByName( "Amy" );
		assertNotNull( villager );
		Long villagerId = villager.getId();

		long newVillageId = 201L;
		villager.setVillageId( newVillageId );
		villagerDao.update( villager );

		Villager finalVillager = villagerDao.selectOneById( villagerId );
		assertNotNull( finalVillager );
		assertEquals( newVillageId, finalVillager.getVillageId().longValue() );
	}

	@Test
	public void testUpdateFailNonExisting() {
		Villager villager = Villager.builder()
				.id( 1L )
				.name( "Red Death" )
				.build();
		Villager response = villagerDao.update( villager );
		assertNull( response );

		Villager finalVillager = villagerDao.selectOneById( 1L );
		assertNull( finalVillager );
	}

	@Test
	public void testDeleteFail() {
		Villager deleteVillager = Villager.builder()
				.id( 1L )
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
