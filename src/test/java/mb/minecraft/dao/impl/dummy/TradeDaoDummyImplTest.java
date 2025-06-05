package mb.minecraft.dao.impl.dummy;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;

import mb.minecraft.dao.DaoConstraintException;
import mb.minecraft.model.Trade;
import mb.minecraft.model.Villager;


@RunWith(MockitoJUnitRunner.class)
public class TradeDaoDummyImplTest {

	private static final Logger logger = LogManager.getLogger( TradeDaoDummyImplTest.class );

	@InjectMocks
	TradeDaoDummyImpl tradeDao;


	@Test
	public void testSelectOneById() {
		Trade trade = tradeDao.selectOneById( 15 );
		assertNotNull( trade );
		assertEquals( 15, trade.getId().intValue() );
		assertEquals( 705, trade.getVillagerId().intValue() );
		assertEquals( 3, trade.getTradeSeqno().intValue() );
	}

	@Test
	public void testSelectAll() {
		List<Trade> trades = tradeDao.selectAll();
		assertNotNull( trades );
		assertEquals( 15, trades.size() );

		Trade t1 = trades.stream()
				.filter( v -> v.getId().equals( 3 ) )
				.findFirst()
				.get();
		assertEquals( 3, t1.getId().intValue() );
		assertEquals( 701, t1.getVillagerId().intValue() );
		assertEquals( 1, t1.getTradeSeqno().intValue() );

		Trade t2 = trades.stream()
				.filter( v -> v.getId().equals( 9 ) )
				.findFirst()
				.get();
		assertEquals( 9, t2.getId().intValue() );
		assertEquals( 73, t2.getVillagerId().intValue() );
		assertEquals( 1, t2.getTradeSeqno().intValue() );
	}

	@Test
	public void testSelectAllForVillager() {
		List<Trade> trades = tradeDao.selectAll( Villager.builder().id( 73 ).build() );
		assertNotNull( trades );
		assertEquals( 4, trades.size() );

		Trade t1 = trades.get(0);
		assertEquals( 9, t1.getId().intValue() );
		assertEquals( 73, t1.getVillagerId().intValue() );
		assertEquals( 1, t1.getTradeSeqno().intValue() );
	}

	@Test
	public void testInsertOneSuccess() {
		int count = tradeDao.selectAll().size();

		Trade newTrade = Trade.builder()
				.villagerId( 1111 )
				.tradeSeqno( 1 )
				.build();
		Trade trade = tradeDao.insertOne( newTrade );
		assertNotNull( trade );
		assertTrue( trade.getId().intValue() > 0 );
		assertEquals( 1111, trade.getVillagerId().intValue() );
		assertEquals( 1, trade.getTradeSeqno().intValue() );
		assertEquals( count + 1, tradeDao.selectAll().size() );
	}

	@Test
	public void testInsertOneIdFail() {
		Trade newTrade = Trade.builder()
				.id( 1 )
				.villagerId( 1111 )
				.tradeSeqno( 1 )
				.build();
		Exception e = assertThrows( DaoConstraintException.class, () -> tradeDao.insertOne( newTrade ) );
		assertNotNull( e );
		assertEquals( "Dataset of type \"Trade\" does not allow duplicate values for field \"ID\"", e.getMessage() );
		logger.error( e.getMessage() );
	}

	@Test
	public void testInsertOneSpecifiedId() {
		Trade newTrade = Trade.builder()
				.id( 90000 )
				.villagerId( 1111 )
				.tradeSeqno( 1 )
				.build();
		Trade trade = tradeDao.insertOne( newTrade );
		assertNotNull( trade );
		assertEquals( 90000, trade.getId().intValue() );
		assertEquals( 1111, trade.getVillagerId().intValue() );
		assertEquals( 1, trade.getTradeSeqno().intValue() );
	}

	@Test
	public void testInsertMany() {
		int count = tradeDao.selectAll().size();

		List<Trade> newTrades = Arrays.asList(
			Trade.builder()
				.villagerId( 1 )
				.tradeSeqno( 1 )
				.build()
			,
			Trade.builder()
				.villagerId( 1 )
				.tradeSeqno( 2 )
				.build()
		);
		List<Trade> insertedTrades = tradeDao.insert( newTrades );
		assertNotNull( insertedTrades );
		assertEquals( 2, insertedTrades.size() );
		assertEquals( count + 2, tradeDao.selectAll().size() );
	}

	@Test
	public void testUpdateExisting() {
		Trade trade = tradeDao.selectOneById( 12 );
		assertNotNull( trade );
		int tradeId = trade.getId();

		trade.setVillagerId( 703 );
		trade.setTradeSeqno( 1 );
		tradeDao.update( trade );

		Trade finalTrade = tradeDao.selectOneById( tradeId );
		assertNotNull( finalTrade );
		assertEquals( 703, finalTrade.getVillagerId().intValue() );
	}

	@Test
	public void testUpdateFailNonExisting() {
		Trade trade = Trade.builder()
				.id( 1001 )
				.villagerId( 1001 )
				.tradeSeqno( 1001 )
				.build();
		Trade response = tradeDao.update( trade );
		assertNull( response );

		Trade finalTrade = tradeDao.selectOneById( 1001 );
		assertNull( finalTrade );
	}

	@Test
	public void testDeleteFail() {
		Trade deleteTrade = Trade.builder()
				.id( 1001 )
				.build();
		boolean wasDeleted = tradeDao.deleteOne( deleteTrade );
		assertFalse( wasDeleted );
	}

	@Test
	public void testDeleteSuccess() {
		int count = tradeDao.selectAll().size();
		
		Trade trade = tradeDao.selectOneById( 15 );
		assertNotNull( trade );

		boolean wasDeleted = tradeDao.deleteOne( trade );
		assertTrue( wasDeleted );
		assertEquals( count - 1, tradeDao.selectAll().size() );
	}

}
