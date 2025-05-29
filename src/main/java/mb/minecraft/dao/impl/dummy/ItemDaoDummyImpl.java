package mb.minecraft.dao.impl.dummy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.annotation.PostConstruct;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Repository;

import mb.minecraft.dao.DaoConstraintException;
import mb.minecraft.dao.ItemDao;
import mb.minecraft.model.Item;

/**
 *
 * @author mikebro
 */
@Repository
public class ItemDaoDummyImpl implements ItemDao {

	private static final Logger logger = LogManager.getLogger( ItemDaoDummyImpl.class );

	private Map<Long,Item> itemTable;
	private long idSeq;


	@PostConstruct
	public void init() {
		logger.info( "ItemDaoDummyImpl.init()" );
	}


	private ItemDaoDummyImpl() {
		logger.info( "ItemDaoDummyImpl constructor" );
		this.itemTable = new HashMap<>();
		this.idSeq = 0L;
		for( Item item : generateMockData() ) {
			insertOne( item );
		}
	}

	@Override
	public Item selectOneById( Long id ) {
		return itemTable.get( id );
	}

	@Override
	public Item selectOneByName( String name ) {
		Optional<Item> optionalItem = itemTable.values().stream()
				.filter( i -> i.getName().equals( name ) )
				.findFirst();
		if( optionalItem.isPresent() )
			return optionalItem.get();
		else
			return null;
	}

	@Override
	public List<Item> selectAll() {
		return new ArrayList<Item>( itemTable.values() );
	}

	@Override
	public Item insertOne( Item newRow ) {
		newRow.setId( deriveId( newRow ) );
		testUniqueIdConstraint( newRow );
		testUniqueNameConstraint( newRow );
		itemTable.put( newRow.getId(), newRow );
		return newRow;
	}

	@Override
	public Item update( Item item ) {
		if( this.itemTable.containsKey( item.getId() ) ) {
			this.itemTable.put( item.getId(), item );
			return item;
		}
		return null;
	}

	@Override
	public boolean deleteOne( Item item ) {
		if( itemTable.containsKey( item.getId() ) ) {
			itemTable.remove( item.getId() );
			return true;
		} else {
			return false;
		}
	}

	@Override
	public void destroy() throws Exception {
		logger.info( "Shutting down ItemDaoDummyImpl" );
	}



	private long deriveId( Item newRow ) {
		long newId = newRow.getId() != null ? newRow.getId() : 0;
		if( idSeq <= newId ) {
			idSeq = newId + 1;
			return newId;
		}
		if( newId > 0 )
			return newId;
		else
			return idSeq++;
	}

	private void testUniqueIdConstraint( Item row ) {
		if( this.itemTable.containsKey( row.getId() ) ) {
			throw new DaoConstraintException(
					String.format( DaoConstraintException.UNIQUE_CONSTRAINT_ERROR, "Item", "ID" ), row );
		}
	}

	private void testUniqueNameConstraint( Item row ) {
		Item item = this.selectOneByName( row.getName() );
		if( item != null ) {
			throw new DaoConstraintException(
					String.format( DaoConstraintException.UNIQUE_CONSTRAINT_ERROR, "Item", "NAME" ), row );
		}
	}



	private List<Item> generateMockData() {
		List<Item> list = new ArrayList<>();
		list.add( generateObject( 1001L, "Emerald", "https://minecraft.wiki/images/Emerald_JE3_BE3.png" ) );
		list.add( generateObject( 1002L, "Rotten Flesh", "https://minecraft.wiki/images/Rotten_Flesh_JE3_BE2.png" ) );
		list.add( generateObject( 1003L, "Gold Ingot", "https://minecraft.wiki/images/Gold_Ingot_JE4_BE2.png" ) );
		list.add( generateObject( 1004L, "Redstone Dust", "https://minecraft.wiki/images/thumb/Redstone_Dust_JE2_BE2.png/150px-Redstone_Dust_JE2_BE2.png" ) );
		list.add( generateObject( 1005L, "Lapis Lazuli", "https://minecraft.wiki/images/Lapis_Lazuli_JE2_BE2.png" ) );
		list.add( generateObject( 1006L, "Ender Pearl", "https://minecraft.wiki/images/Ender_Pearl_JE3_BE2.png" ) );
		list.add( generateObject( 1007L, "Glowstone", "https://minecraft.wiki/images/thumb/Glowstone_JE4_BE2.png/150px-Glowstone_JE4_BE2.png" ) );
		list.add( generateObject( 1008L, "Bottle o' Enchanting", "https://minecraft.wiki/images/Bottle_o%27_Enchanting.gif" ) );
		list.add( generateObject( 1009L, "Paper", "https://minecraft.wiki/images/Paper_JE2_BE2.png" ) );
		list.add( generateObject( 1010L, "Enchanted Book", "https://minecraft.wiki/images/Enchanted_Book.gif" ) );
		list.add( generateObject( 1011L, "Book", "https://minecraft.wiki/images/Book_JE2_BE2.png" ) );
		list.add( generateObject( 1012L, "Compass", "https://minecraft.wiki/images/Compass_JE3_BE3.gif" ) );
		list.add( generateObject( 1013L, "Bookshelf", "https://minecraft.wiki/images/thumb/Bookshelf_JE4_BE2.png/150px-Bookshelf_JE4_BE2.png" ) );
		list.add( generateObject( 1014L, "Written Book", "https://minecraft.wiki/images/Written_Book_JE2_BE2.gif" ) );
		list.add( generateObject( 1015L, "Clock", "https://minecraft.wiki/images/Clock_JE3_BE3.gif" ) );
		list.add( generateObject( 1016L, "Glass", "https://minecraft.wiki/images/Glass_JE4_BE2.png" ) );
		list.add( generateObject( 1017L, "Wheat", "https://minecraft.wiki/images/Wheat_JE2_BE2.png" ) );
		list.add( generateObject( 1018L, "Raw Beef", "https://minecraft.wiki/images/Raw_Beef_JE4_BE3.png" ) );
		return list;
	}

	private Item generateObject( Long id, String name, String imageSource ) {
		return Item.builder()
				.id( id )
				.name( name )
				.imageSource( imageSource )
				.build();
	}
}
