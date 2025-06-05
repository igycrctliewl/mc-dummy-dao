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
		VillagerType villagerType = villagerTypeDao.selectOneById( 101 );
		assertNotNull( villagerType );
		assertEquals( 101, villagerType.getId().intValue() );
		assertEquals( "Librarian", villagerType.getProfession() );
	}

	@Test
	public void testSelectOneByName() {
		VillagerType villagerType = villagerTypeDao.selectOneByName( "Leatherworker" );
		assertNotNull( villagerType );
		assertEquals( 102, villagerType.getId().intValue() );
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
		assertEquals( 103, v1.getId().intValue() );

		VillagerType v2 = villagerTypes.stream()
				.filter( v -> v.getProfession().equals( "Butcher" ) )
				.findFirst()
				.get();
		assertEquals( 104, v2.getId().intValue() );
	}

	@Test
	public void testInsertOneSuccess() {
		VillagerType newVillagerType = VillagerType.builder()
				.id( 10 )
				.profession( "Blacksmith" )
				.build();
		VillagerType villagerType = villagerTypeDao.insertOne( newVillagerType );
		assertNotNull( villagerType );
		assertEquals( 10, villagerType.getId().intValue() );
		assertEquals( "Blacksmith", villagerType.getProfession() );
	}

	@Test
	public void testInsertOneIdFail() {
		VillagerType newVillagerType = VillagerType.builder()
				.id( 104 )
				.profession( "Ice Cream Man" )
				.build();
		Exception e = assertThrows( DaoConstraintException.class, () -> villagerTypeDao.insertOne( newVillagerType ) );
		assertNotNull( e );
		assertEquals( "Dataset of type \"VillagerType\" does not allow duplicate values for field \"ID\"", e.getMessage() );
	}

	@Test
	public void testInsertOneNameFail() {
		VillagerType newVillagerType = VillagerType.builder()
				.id( 5 )
				.profession( "Shepherd" )
				.build();
		Exception e = assertThrows( DaoConstraintException.class, () -> villagerTypeDao.insertOne( newVillagerType ) );
		assertNotNull( e );
		assertEquals( "Dataset of type \"VillagerType\" does not allow duplicate values for field \"PROFESSION\"", e.getMessage() );
	}

	@Test
	public void testInsertOneSpecifiedId() {
		VillagerType newVillagerType = VillagerType.builder()
				.id( 90000 )
				.profession( "Farmer" )
				.build();
		VillagerType villagerType = villagerTypeDao.insertOne( newVillagerType );
		assertNotNull( villagerType );
		assertEquals( 90000, villagerType.getId().intValue() );
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
		assertTrue( villagerType.getId().intValue() > 0 );
	}

	@Test
	public void testDeleteFail() {
		when( villagerDao.selectAll() ).thenReturn( prepareVillagers() );
		VillagerType deleteVillagerType = VillagerType.builder()
				.id( 1 )
				.profession( "Cartographer" )
				.build();
		boolean wasDeleted = villagerTypeDao.deleteOne( deleteVillagerType );
		assertFalse( wasDeleted );
	}

	@Test
	public void testDeleteException() {
		when( villagerDao.selectAll() ).thenReturn( prepareVillagers() );
		VillagerType deleteVillagerType = VillagerType.builder()
				.id( 105 )
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
		list.add( generateObject( 699, "Liam Z", true, 105 ) );
		list.add( generateObject( 701, "Gary", false, 105 ) );
		list.add( generateObject( 702, "Malcolm", false, 104 ) );
		list.add( generateObject( 73, "Tyler", false, null ) );
		list.add( generateObject( 704, "Dana", false, 104 ) );
		list.add( generateObject( 705, "Amy", false, 101 ) );
		return list;
	}

	private static Villager generateObject( Integer id, String name, boolean isTagged, Integer typeId ) {
		return Villager.builder()
				.id( id )
				.name( name )
				.tagged( isTagged )
				.typeId( typeId )
				.build();
	}

}
