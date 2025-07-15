package mb.minecraft.dao.impl.dummy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Repository;

import mb.minecraft.dao.DaoConstraintException;
import mb.minecraft.dao.TradeItemDao;
import mb.minecraft.model.Item;
import mb.minecraft.model.OfferRequire;
import mb.minecraft.model.Trade;
import mb.minecraft.model.TradeItem;

/**
 *
 * @author mikebro
 */
@Repository
public class TradeItemDaoDummyImpl implements TradeItemDao {

	private static final Logger logger = LogManager.getLogger( TradeItemDaoDummyImpl.class );

	private Map<Integer,TradeItem> tradeItemTable;
	private int idSeq;


	@PostConstruct
	public void init() {
		logger.info( "TradeItemDaoDummyImpl.init()" );
	}


	private TradeItemDaoDummyImpl() {
		logger.info( "TradeItemDaoDummyImpl constructor" );
		this.idSeq = 0;
		this.tradeItemTable = new HashMap<>();
		for( TradeItem item : generateMockData() ) {
			insertOne( item );
		}
	}

	// I would like this method to return a new List, not the private List for this object
	// This is NOT meant to be a getter method for the table
	@Override
	public List<TradeItem> selectAll() {
		return tradeItemTable.values().stream().collect( Collectors.toList() );
	}

	@Override
	public List<TradeItem> selectAll( Trade trade ) {
		return tradeItemTable.values().stream()
				.filter( ti -> ti.getTradeId().equals( trade.getId() ) )
				.collect( Collectors.toList() ); 
	}

	@Override
	public List<TradeItem> selectAll( Item item ) {
		return tradeItemTable.values().stream()
				.filter( ti -> ti.getItemId().equals( item.getId() ) )
				.collect( Collectors.toList() ); 
	}

	@Override
	public TradeItem insertOne( TradeItem newRow ) {
		newRow.setId( deriveId( newRow ) );
		testUniqueIdConstraint( newRow );
		testUniqueTradeKeysConstraint( newRow );
		tradeItemTable.put( newRow.getId(), newRow );
		return newRow;
	}

	@Override
	public List<TradeItem> insert( List<TradeItem> newRowSet ) {
		newRowSet.forEach( row -> insertOne( row ) );
		return newRowSet;
	}

	@Override
	public TradeItem update( TradeItem item ) {
		if( tradeItemTable.containsKey( item.getId() ) ) {
			tradeItemTable.put( item.getId(), item );
			return item;
		} else {
			return null;
		}
	}

	@Override
	public boolean deleteOne( TradeItem item ) {
		if( tradeItemTable.containsKey( item.getId() ) ) {
			tradeItemTable.remove( item.getId() );
			return true;
		} else {
			return false;
		}
	}

	@Override
	public void destroy() throws Exception {
		logger.info( "Shutting down TradeItemDaoDummyImpl" );
	}



	private int deriveId( TradeItem newRow ) {
		int newId = newRow.getId() != null ? newRow.getId() : 0;
		if( idSeq <= newId ) {
			idSeq = newId + 1;
			return newId;
		}
		if( newId > 0 )
			return newId;
		else
			return idSeq++;
	}

	private void testUniqueIdConstraint( TradeItem row ) {
		if( tradeItemTable.containsKey( row.getId() ) ) {
			throw new DaoConstraintException(
					String.format( DaoConstraintException.UNIQUE_CONSTRAINT_ERROR, "TradeItem", "Id" ), row );
		}
	}

	private void testUniqueTradeKeysConstraint( TradeItem row ) {
		Optional<TradeItem> searchedItem = findMatching( row );
		if( searchedItem.isPresent() ) {
			throw new DaoConstraintException(
					String.format( DaoConstraintException.UNIQUE_CONSTRAINT_ERROR, "TradeItem", "TradeId-OfferRequire-Seqno" ), row );
		}
	}

	private Optional<TradeItem> findMatching( TradeItem item ) {
		return tradeItemTable.values().stream()
				.filter( ti -> ti.getTradeId().equals( item.getTradeId() ) &&
						ti.getOfferRequire().equals( item.getOfferRequire() ) &&
						ti.getSeqno().equals( item.getSeqno() ) )
				.findFirst();
	}


	private List<TradeItem> generateMockData() {
		List<TradeItem> list = new ArrayList<>();
		list.add( generateObject( 1, "O", 1, 1, 1001, null ) );
		list.add( generateObject( 1, "R", 1, 24, 1009, null ) );
		list.add( generateObject( 2, "O", 1, 1, 1010, "Punch I" ) );
		list.add( generateObject( 2, "R", 1, 1, 1011, null ) );
		list.add( generateObject( 2, "R", 2, 15, 1001, null ) );
		list.add( generateObject( 3, "O", 1, 1, 1001, null ) );
		list.add( generateObject( 3, "R", 1, 10, 1011, null ) );
		list.add( generateObject( 4, "O", 1, 1, 1012, null ) );
		list.add( generateObject( 4, "R", 1, 11, 1001, null ) );
		list.add( generateObject( 5, "O", 1, 1, 1013, null ) );
		list.add( generateObject( 5, "R", 1, 4, 1001, null ) );
		list.add( generateObject( 6, "O", 1, 1, 1001, null ) );
		list.add( generateObject( 6, "R", 1, 2, 1014, null ) );
		list.add( generateObject( 7, "O", 1, 1, 1015, null ) );
		list.add( generateObject( 7, "R", 1, 10, 1001, null ) );
		list.add( generateObject( 8, "O", 1, 4, 1016, null ) );
		list.add( generateObject( 8, "R", 1, 1, 1001, null ) );
		list.add( generateObject( 9, "O", 1, 1, 1001, null ) );
		list.add( generateObject( 9, "R", 1, 40, 1002, null ) );
		list.add( generateObject( 10, "O", 1, 1, 1001, null ) );
		list.add( generateObject( 10, "R", 1, 10, 1003, null ) );
		list.add( generateObject( 11, "O", 1, 4, 1004, null ) );
		list.add( generateObject( 11, "R", 1, 1, 1001, null ) );
		list.add( generateObject( 12, "O", 1, 2, 1005, null ) );
		list.add( generateObject( 12, "R", 1, 1, 1001, null ) );
		list.add( generateObject( 13, "O", 1, 1, 1006, null ) );
		list.add( generateObject( 13, "R", 1, 5, 1001, null ) );
		list.add( generateObject( 14, "O", 1, 3, 1007, null ) );
		list.add( generateObject( 14, "R", 1, 1, 1001, null ) );
		list.add( generateObject( 15, "O", 1, 1, 1008, null ) );
		list.add( generateObject( 15, "R", 1, 8, 1001, null ) );
		return list;
	}

	private TradeItem generateObject( int tradeId, String offReq, int seqno, int quantity, int itemId, String memo ) {
		return TradeItem.builder()
				.tradeId( tradeId )
				.offerRequire( OfferRequire.getFromCode( offReq ) )
				.seqno( seqno )
				.quantity( quantity )
				.itemId( itemId )
				.memo( memo )
				.build();
	}
}
