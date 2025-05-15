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
		if( idSeq < newId ) {
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
		list.add( generateObject( 1007L, "Emerald", "https://minecraft.wiki/images/Emerald_JE3_BE3.png" ) );
		list.add( generateObject( 1008L, "Wheat", "https://minecraft.wiki/images/Wheat_JE2_BE2.png" ) );
		list.add( generateObject( 1009L, "Paper", "https://minecraft.wiki/images/Paper_JE2_BE2.png" ) );
		list.add( generateObject( 1010L, "Rotten Flesh", "https://minecraft.wiki/images/Rotten_Flesh_JE3_BE2.png" ) );
		list.add( generateObject( 1011L, "Raw Beef", "https://minecraft.wiki/images/Raw_Beef_JE4_BE3.png" ) );
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
