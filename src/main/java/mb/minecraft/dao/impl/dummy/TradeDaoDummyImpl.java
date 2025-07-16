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

	private Map<Integer,Trade> tradeTable;
	private int idSeq;


	@PostConstruct
	public void init() {
		logger.info( "TradeDaoDummyImpl.init()" );
	}


	private TradeDaoDummyImpl() {
		logger.info( "TradeDaoDummyImpl constructor" );
		tradeTable = new HashMap<>();
		idSeq = 0;
		for( Trade trade : generateMockData() ) {
			insertOne( trade );
		}
	}

	@Override
	public Trade selectOneById( int id ) {
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
		deriveSeqno( newRow );
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



	private int deriveId( Trade newRow ) {
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

	private void deriveSeqno( Trade newRow ) {
		if( newRow.getTradeSeqno() == null && newRow.getVillagerId() != null ) {
			int seqno = 0;
			for( Trade t : selectAll( Villager.builder().id( newRow.getVillagerId() ).build() ) ) {
				if( t.getTradeSeqno() > seqno ) {
					seqno = t.getTradeSeqno();
				}
			}
			seqno++;
			newRow.setTradeSeqno( seqno );
		}
	}

	private void testUniqueIdConstraint( Trade row ) {
		if( tradeTable.containsKey( row.getId() ) ) {
			throw new DaoConstraintException(
					String.format( DaoConstraintException.UNIQUE_CONSTRAINT_ERROR, "Trade", "ID" ), row );
		}
	}



	private List<Trade> generateMockData() {
		List<Trade> list = new ArrayList<>();
		list.add( generateObject( 1, 699, 1 ) );
		list.add( generateObject( 2, 699, 2 ) );
		list.add( generateObject( 3, 701, 1 ) );
		list.add( generateObject( 4, 701, 2 ) );
		list.add( generateObject( 5, 702, 1 ) );
		list.add( generateObject( 6, 702, 2 ) );
		list.add( generateObject( 7, 704, 1 ) );
		list.add( generateObject( 8, 704, 2 ) );
		list.add( generateObject( 9, 73, 1 ) );
		list.add( generateObject( 10, 73, 2 ) );
		list.add( generateObject( 11, 73, 3 ) );
		list.add( generateObject( 12, 73, 4 ) );
		list.add( generateObject( 13, 705, 1 ) );
		list.add( generateObject( 14, 705, 2 ) );
		list.add( generateObject( 15, 705, 3 ) );
		return list;
	}

	private Trade generateObject( Integer id, Integer villagerId, int tradeSeqno ) {
		return Trade.builder()
				.id( id )
				.villagerId( villagerId )
				.tradeSeqno( tradeSeqno )
				.build();
	}
}
