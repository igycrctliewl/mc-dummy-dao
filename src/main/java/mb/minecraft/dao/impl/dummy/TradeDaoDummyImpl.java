package mb.minecraft.dao.impl.dummy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Repository;

import mb.minecraft.dao.DaoConstraintException;
import mb.minecraft.dao.TradeDao;
import mb.minecraft.model.Trade;
import mb.minecraft.model.Villager;

/**
 *
 * @author mikebro
 */
@Repository
public class TradeDaoDummyImpl implements TradeDao {

	private static final Logger logger = LogManager.getLogger( TradeDaoDummyImpl.class );

	private Map<Long,Trade> tradeTable;
	private long idSeq;


	@PostConstruct
	public void init() {
		logger.info( "TradeDaoDummyImpl.init()" );
	}


	private TradeDaoDummyImpl() {
		logger.info( "TradeDaoDummyImpl constructor" );
		tradeTable = new HashMap<>();
		idSeq = 0L;
		for( Trade trade : generateMockData() ) {
			insertOne( trade );
		}
	}

	@Override
	public Trade selectOneById( Long id ) {
		return tradeTable.get( id );
	}

	@Override
	public List<Trade> selectAll() {
		return new ArrayList<Trade>( tradeTable.values() );
	}

	@Override
	public List<Trade> selectAll( Villager villager ) {
		return tradeTable.values().stream()
				.filter( trade -> trade.getVillagerId().equals( villager.getId() ) )
				.collect( Collectors.toList() );
	}

	@Override
	public Trade insertOne( Trade newRow ) {
		newRow.setId( deriveId( newRow ) );
		testUniqueIdConstraint( newRow );
		tradeTable.put( newRow.getId(), newRow );
		return newRow;
	}

	@Override
	public List<Trade> insert( List<Trade> newRowSet ) {
		newRowSet.forEach( row -> insertOne( row ) );
		return newRowSet;
	}

	@Override
	public Trade update( Trade trade ) {
		if( this.tradeTable.containsKey( trade.getId() ) ) {
			this.tradeTable.put( trade.getId(), trade );
			return trade;
		}
		return null;
	}

	@Override
	public boolean deleteOne( Trade trade ) {
		if( tradeTable.containsKey( trade.getId() ) ) {
			tradeTable.remove( trade.getId() );
			return true;
		} else {
			return false;
		}
	}

	@Override
	public void destroy() throws Exception {
		logger.info( "Shutting down TradeDaoDummyImpl" );
	}



	private long deriveId( Trade newRow ) {
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

	private void testUniqueIdConstraint( Trade row ) {
		if( tradeTable.containsKey( row.getId() ) ) {
			throw new DaoConstraintException(
					String.format( DaoConstraintException.UNIQUE_CONSTRAINT_ERROR, "Trade", "ID" ), row );
		}
	}



	private List<Trade> generateMockData() {
		List<Trade> list = new ArrayList<>();
		list.add( generateObject( 1L, 699L, 1 ) );
		list.add( generateObject( 2L, 699L, 2 ) );
		list.add( generateObject( 3L, 701L, 1 ) );
		list.add( generateObject( 4L, 701L, 2 ) );
		list.add( generateObject( 5L, 702L, 1 ) );
		list.add( generateObject( 6L, 702L, 2 ) );
		list.add( generateObject( 7L, 704L, 1 ) );
		list.add( generateObject( 8L, 704L, 2 ) );
		list.add( generateObject( 9L, 73L, 1 ) );
		list.add( generateObject( 10L, 73L, 2 ) );
		list.add( generateObject( 11L, 73L, 3 ) );
		list.add( generateObject( 12L, 73L, 4 ) );
		list.add( generateObject( 13L, 705L, 1 ) );
		list.add( generateObject( 14L, 705L, 2 ) );
		list.add( generateObject( 15L, 705L, 3 ) );
		return list;
	}

	private Trade generateObject( Long id, Long villagerId, int tradeSeqno ) {
		return Trade.builder()
				.id( id )
				.villagerId( villagerId )
				.tradeSeqno( tradeSeqno )
				.build();
	}
}
