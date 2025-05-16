package mb.minecraft.dao.impl.dummy;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import mb.minecraft.dao.DaoConstraintException;
import mb.minecraft.dao.ForeignKeyConstraintException;
import mb.minecraft.dao.VillagerDao;
import mb.minecraft.model.Villager;
import mb.minecraft.model.VillagerType;


@RunWith(MockitoJUnitRunner.class)
public class VillagerTypeDaoDummyImplTest {

	private static final Logger logger = LogManager.getLogger( VillagerTypeDaoDummyImplTest.class );

	@InjectMocks
	VillagerTypeDaoDummyImpl villagerTypeDao;

	@Mock
	VillagerDao villagerDao;


	@Test
	public void testSelectOneById() {
		VillagerType villagerType = villagerTypeDao.selectOneById( 101L );
		assertNotNull( villagerType );
		assertEquals( 101L, villagerType.getId().longValue() );
		assertEquals( "Librarian", villagerType.getProfession() );
	}

	@Test
	public void testSelectOneByName() {
		VillagerType villagerType = villagerTypeDao.selectOneByName( "Leatherworker" );
		assertNotNull( villagerType );
		assertEquals( 102L, villagerType.getId().longValue() );
		assertEquals( "Leatherworker", villagerType.getProfession() );
	}

	@Test
	public void testSelectAll() {
		List<VillagerType> villagerTypes = villagerTypeDao.selectAll();
		assertNotNull( villagerTypes );
		assertEquals( 5, villagerTypes.size() );

		VillagerType v1 = villagerTypes.stream()
				.filter( v -> v.getProfession().equals( "Shepherd" ) )
				.findFirst()
				.get();
		assertEquals( 103L, v1.getId().longValue() );

		VillagerType v2 = villagerTypes.stream()
				.filter( v -> v.getProfession().equals( "Butcher" ) )
				.findFirst()
				.get();
		assertEquals( 104L, v2.getId().longValue() );
	}

	@Test
	public void testInsertOneSuccess() {
		VillagerType newVillagerType = VillagerType.builder()
				.id( 10L )
				.profession( "Blacksmith" )
				.build();
		VillagerType villagerType = villagerTypeDao.insertOne( newVillagerType );
		assertNotNull( villagerType );
		assertEquals( 10L, villagerType.getId().longValue() );
		assertEquals( "Blacksmith", villagerType.getProfession() );
	}

	@Test
	public void testInsertOneIdFail() {
		VillagerType newVillagerType = VillagerType.builder()
				.id( 104L )
				.profession( "Ice Cream Man" )
				.build();
		Exception e = assertThrows( DaoConstraintException.class, () -> villagerTypeDao.insertOne( newVillagerType ) );
		assertNotNull( e );
		assertEquals( "Dataset of type \"VillagerType\" does not allow duplicate values for field \"ID\"", e.getMessage() );
	}

	@Test
	public void testInsertOneNameFail() {
		VillagerType newVillagerType = VillagerType.builder()
				.id( 5L )
				.profession( "Shepherd" )
				.build();
		Exception e = assertThrows( DaoConstraintException.class, () -> villagerTypeDao.insertOne( newVillagerType ) );
		assertNotNull( e );
		assertEquals( "Dataset of type \"VillagerType\" does not allow duplicate values for field \"PROFESSION\"", e.getMessage() );
	}

	@Test
	public void testInsertOneSpecifiedId() {
		VillagerType newVillagerType = VillagerType.builder()
				.id( 90000L )
				.profession( "Farmer" )
				.build();
		VillagerType villagerType = villagerTypeDao.insertOne( newVillagerType );
		assertNotNull( villagerType );
		assertEquals( 90000L, villagerType.getId().longValue() );
		assertEquals( "Farmer", villagerType.getProfession() );
	}

	@Test
	public void testInsertOneDerivedId() {
		VillagerType newVillagerType = VillagerType.builder()
				.profession( "Fisherman" )
				.build();
		VillagerType villagerType = villagerTypeDao.insertOne( newVillagerType );
		assertNotNull( villagerType );
		assertNotNull( villagerType.getId() );
		assertTrue( villagerType.getId().longValue() > 0 );
	}

	@Test
	public void testDeleteFail() {
		when( villagerDao.selectAll() ).thenReturn( prepareVillagers() );
		VillagerType deleteVillagerType = VillagerType.builder()
				.id( 1L )
				.profession( "Cartographer" )
				.build();
		boolean wasDeleted = villagerTypeDao.deleteOne( deleteVillagerType );
		assertFalse( wasDeleted );
	}

	@Test
	public void testDeleteException() {
		when( villagerDao.selectAll() ).thenReturn( prepareVillagers() );
		VillagerType deleteVillagerType = VillagerType.builder()
				.id( 105L )
				.profession( "Cleric" )
				.build();
		Exception e = assertThrows( ForeignKeyConstraintException.class, () -> villagerTypeDao.deleteOne( deleteVillagerType ) );
		logger.error( e.getMessage() );
	}

	@Test
	public void testDeleteSuccess() {
		when( villagerDao.selectAll() ).thenReturn( prepareVillagers() );

		int count = villagerTypeDao.selectAll().size();

		VillagerType villagerType = villagerTypeDao.selectOneByName( "Shepherd" );
		assertNotNull( villagerType );

		boolean wasDeleted = villagerTypeDao.deleteOne( villagerType );
		assertTrue( wasDeleted );
		assertEquals( count - 1, villagerTypeDao.selectAll().size() );
	}



	private static List<Villager> prepareVillagers() {
		List<Villager> list = new ArrayList<>();
		list.add( generateObject( 699L, "Liam Z", true, 105L ) );
		list.add( generateObject( 701L, "Gary", false, 105L ) );
		list.add( generateObject( 702L, "Malcolm", false, 104L ) );
		list.add( generateObject( 73L, "Tyler", false, null ) );
		list.add( generateObject( 704L, "Dana", false, 104L ) );
		list.add( generateObject( 705L, "Amy", false, 101L ) );
		return list;
	}

	private static Villager generateObject( Long id, String name, boolean isTagged, Long typeId ) {
		return Villager.builder()
				.id( id )
				.name( name )
				.tagged( isTagged )
				.typeId( typeId )
				.build();
	}

}
