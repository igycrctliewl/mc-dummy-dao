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
import mb.minecraft.model.Item;
import mb.minecraft.model.Trade;
import mb.minecraft.model.Villager;


@RunWith(MockitoJUnitRunner.class)
public class TradeDaoDummyImplTest {

	private static final Logger logger = LogManager.getLogger( TradeDaoDummyImplTest.class );

	@InjectMocks
	TradeDaoDummyImpl tradeDao;


	@Test
	public void testSelectOneById() {
		Trade trade = tradeDao.selectOneById( 15L );
		assertNotNull( trade );
		assertEquals( 15L, trade.getId().longValue() );
		assertEquals( 705L, trade.getVillagerId().longValue() );
		assertEquals( 3, trade.getTradeSeqno().intValue() );
	}

	@Test
	public void testSelectAll() {
		List<Trade> trades = tradeDao.selectAll();
		assertNotNull( trades );
		assertEquals( 15, trades.size() );

		Trade t1 = trades.stream()
				.filter( v -> v.getId().equals( 3L ) )
				.findFirst()
				.get();
		assertEquals( 3L, t1.getId().longValue() );
		assertEquals( 701L, t1.getVillagerId().longValue() );
		assertEquals( 1, t1.getTradeSeqno().intValue() );

		Trade t2 = trades.stream()
				.filter( v -> v.getId().equals( 9L ) )
				.findFirst()
				.get();
		assertEquals( 9L, t2.getId().longValue() );
		assertEquals( 73L, t2.getVillagerId().longValue() );
		assertEquals( 1, t2.getTradeSeqno().intValue() );
	}

	@Test
	public void testSelectAllForVillager() {
		List<Trade> trades = tradeDao.selectAll( Villager.builder().id( 73L ).build() );
		assertNotNull( trades );
		assertEquals( 4, trades.size() );

		Trade t1 = trades.get(0);
		assertEquals( 9L, t1.getId().longValue() );
		assertEquals( 73L, t1.getVillagerId().longValue() );
		assertEquals( 1, t1.getTradeSeqno().intValue() );
	}

	@Test
	public void testInsertOneSuccess() {
		int count = tradeDao.selectAll().size();

		Trade newTrade = Trade.builder()
				.villagerId( 1111L )
				.tradeSeqno( 1 )
				.build();
		Trade trade = tradeDao.insertOne( newTrade );
		assertNotNull( trade );
		assertTrue( trade.getId().longValue() > 0 );
		assertEquals( 1111L, trade.getVillagerId().longValue() );
		assertEquals( 1, trade.getTradeSeqno().intValue() );
		assertEquals( count + 1, tradeDao.selectAll().size() );
	}

	@Test
	public void testInsertOneIdFail() {
		Trade newTrade = Trade.builder()
				.id( 1L )
				.villagerId( 1111L )
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
				.id( 90000L )
				.villagerId( 1111L )
				.tradeSeqno( 1 )
				.build();
		Trade trade = tradeDao.insertOne( newTrade );
		assertNotNull( trade );
		assertEquals( 90000L, trade.getId().longValue() );
		assertEquals( 1111L, trade.getVillagerId().longValue() );
		assertEquals( 1, trade.getTradeSeqno().intValue() );
	}

	@Test
	public void testInsertMany() {
		int count = tradeDao.selectAll().size();

		List<Trade> newTrades = Arrays.asList(
			Trade.builder()
				.villagerId( 1L )
				.tradeSeqno( 1 )
				.build()
			,
			Trade.builder()
				.villagerId( 1L )
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
		Trade trade = tradeDao.selectOneById( 12L );
		assertNotNull( trade );
		Long tradeId = trade.getId();

		trade.setVillagerId( 703L );
		trade.setTradeSeqno( 1 );
		tradeDao.update( trade );

		Trade finalTrade = tradeDao.selectOneById( tradeId );
		assertNotNull( finalTrade );
		assertEquals( 703L, finalTrade.getVillagerId().longValue() );
	}

	@Test
	public void testUpdateFailNonExisting() {
		Trade trade = Trade.builder()
				.id( 1001L )
				.villagerId( 1001L )
				.tradeSeqno( 1001 )
				.build();
		Trade response = tradeDao.update( trade );
		assertNull( response );

		Trade finalTrade = tradeDao.selectOneById( 1001L );
		assertNull( finalTrade );
	}

	@Test
	public void testDeleteFail() {
		Trade deleteTrade = Trade.builder()
				.id( 1001L )
				.build();
		boolean wasDeleted = tradeDao.deleteOne( deleteTrade );
		assertFalse( wasDeleted );
	}

	@Test
	public void testDeleteSuccess() {
		int count = tradeDao.selectAll().size();
		
		Trade trade = tradeDao.selectOneById( 15L );
		assertNotNull( trade );

		boolean wasDeleted = tradeDao.deleteOne( trade );
		assertTrue( wasDeleted );
		assertEquals( count - 1, tradeDao.selectAll().size() );
	}

}
