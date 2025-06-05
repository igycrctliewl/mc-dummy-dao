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
import mb.minecraft.model.Item;


@RunWith(MockitoJUnitRunner.class)
public class ItemDaoDummyImplTest {

	private static final Logger logger = LogManager.getLogger( ItemDaoDummyImplTest.class );

	@InjectMocks
	ItemDaoDummyImpl itemDao;


	@Test
	public void testSelectOneById() {
		Item item = itemDao.selectOneById( 1001 );
		assertNotNull( item );
		assertEquals( 1001, item.getId().intValue() );
		assertEquals( "Emerald", item.getName() );
		assertEquals( "https://minecraft.wiki/images/Emerald_JE3_BE3.png", item.getImageSource() );
	}

	@Test
	public void testSelectOneByName() {
		Item item = itemDao.selectOneByName( "Rotten Flesh" );
		assertNotNull( item );
		assertEquals( 1002, item.getId().intValue() );
		assertEquals( "Rotten Flesh", item.getName() );
		assertEquals( "https://minecraft.wiki/images/Rotten_Flesh_JE3_BE2.png", item.getImageSource() );
	}

	@Test
	public void testSelectAll() {
		List<Item> items = itemDao.selectAll();
		assertNotNull( items );
		assertEquals( 18, items.size() );

		Item v1 = items.stream()
				.filter( v -> v.getName().equals( "Raw Beef" ) )
				.findFirst()
				.get();
		assertEquals( 1018, v1.getId().intValue() );
		assertEquals( "Raw Beef", v1.getName() );

		Item v2 = items.stream()
				.filter( v -> v.getName().equals( "Paper" ) )
				.findFirst()
				.get();
		assertEquals( 1009, v2.getId().intValue() );
		assertEquals( "Paper", v2.getName() );
	}

	@Test
	public void testInsertOneSuccess() {
		Item newItem = Item.builder()
				.id( 10 )
				.name( "Block of Emerald" )
				.imageSource( "https://minecraft.wiki/images/Block_of_Emerald_JE4_BE3.png" )
				.build();
		Item item = itemDao.insertOne( newItem );
		assertNotNull( item );
		assertEquals( 10, item.getId().intValue() );
		assertEquals( "Block of Emerald", item.getName() );
		assertEquals( "https://minecraft.wiki/images/Block_of_Emerald_JE4_BE3.png", item.getImageSource() );
	}

	@Test
	public void testInsertOneIdFail() {
		Item newItem = Item.builder()
				.id( 1007 )
				.name( "Block of Emerald" )
				.imageSource( "https://minecraft.wiki/images/Block_of_Emerald_JE4_BE3.png" )
				.build();
		Exception e = assertThrows( DaoConstraintException.class, () -> itemDao.insertOne( newItem ) );
		assertNotNull( e );
		assertEquals( "Dataset of type \"Item\" does not allow duplicate values for field \"ID\"", e.getMessage() );
		logger.error( e.getMessage() );
	}

	@Test
	public void testInsertOneNameFail() {
		Item newItem = Item.builder()
				.id( 5 )
				.name( "Wheat" )
				.imageSource( "https://minecraft.wiki/images/Wheat_JE4_BE3.png" )
				.build();
		Exception e = assertThrows( DaoConstraintException.class, () -> itemDao.insertOne( newItem ) );
		assertNotNull( e );
		assertEquals( "Dataset of type \"Item\" does not allow duplicate values for field \"NAME\"", e.getMessage() );
		logger.error( e.getMessage() );
	}

	@Test
	public void testInsertOneSpecifiedId() {
		Item newItem = Item.builder()
				.id( 90000 )
				.name( "Dirt" )
				.imageSource( "https://minecraft.wiki/images/Dirt_JE2_BE2.png" )
				.build();
		Item item = itemDao.insertOne( newItem );
		assertNotNull( item );
		assertEquals( 90000, item.getId().intValue() );
		assertEquals( "Dirt", item.getName() );
		assertEquals( "https://minecraft.wiki/images/Dirt_JE2_BE2.png", item.getImageSource() );
	}

	@Test
	public void testInsertOneDerivedId() {
		Item newItem = Item.builder()
				.name( "Grass Block" )
				.imageSource( "https://minecraft.wiki/images/Grass_Block_JE7_BE6.png" )
				.build();
		Item item = itemDao.insertOne( newItem );
		assertNotNull( item );
		assertNotNull( item.getId() );
		assertTrue( item.getId().intValue() > 0 );
	}

	@Test
	public void testUpdateExisting() {
		Item item = itemDao.selectOneByName( "Emerald" );
		assertNotNull( item );
		int itemId = item.getId();

		String newImageSource = "https://static.wikia.nocookie.net/minecraft_gamepedia/images/2/26/Emerald_JE3_BE3.png";
		item.setImageSource( newImageSource );
		itemDao.update( item );

		Item finalItem = itemDao.selectOneById( itemId );
		assertNotNull( finalItem );
		assertEquals( newImageSource, finalItem.getImageSource() );
	}

	@Test
	public void testUpdateFailNonExisting() {
		Item item = Item.builder()
				.id( 1 )
				.name( "Red Dye" )
				.imageSource( "https://minecraft.wiki/images/Red_Dye_JE3_BE3.png" )
				.build();
		Item response = itemDao.update( item );
		assertNull( response );

		Item finalItem = itemDao.selectOneById( 1 );
		assertNull( finalItem );
	}

	@Test
	public void testDeleteFail() {
		Item deleteItem = Item.builder()
				.id( 1 )
				.name( "Dirt" )
				.build();
		boolean wasDeleted = itemDao.deleteOne( deleteItem );
		assertFalse( wasDeleted );
	}

	@Test
	public void testDeleteSuccess() {
		int count = itemDao.selectAll().size();
		
		Item item = itemDao.selectOneByName( "Emerald" );
		assertNotNull( item );

		boolean wasDeleted = itemDao.deleteOne( item );
		assertTrue( wasDeleted );
		assertEquals( count - 1, itemDao.selectAll().size() );
	}

}
