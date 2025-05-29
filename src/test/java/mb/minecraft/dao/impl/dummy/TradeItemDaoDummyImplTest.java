package mb.minecraft.dao.impl.dummy;

import static mb.minecraft.model.OfferRequire.OFFER;
import static mb.minecraft.model.OfferRequire.REQUIRE;
import static org.junit.Assert.assertEquals;
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
import mb.minecraft.model.TradeItem;


@RunWith(MockitoJUnitRunner.class)
public class TradeItemDaoDummyImplTest {

	private static final Logger logger = LogManager.getLogger( TradeItemDaoDummyImplTest.class );

	@InjectMocks
	TradeItemDaoDummyImpl tradeItemDao;


	@Test
	public void testSelectAll() {
		List<TradeItem> tradeItems = tradeItemDao.selectAll();
		assertNotNull( tradeItems );
		assertEquals( 31, tradeItems.size() );

		TradeItem ti1 = tradeItems.get(2);
		assertEquals( 2L, ti1.getTradeId().longValue() );
		assertEquals( OFFER, ti1.getOfferRequire() );
		assertEquals( 1, ti1.getSeqno().intValue() );
		assertEquals( 1, ti1.getQuantity().intValue() );
		assertEquals( 1010L, ti1.getItemId().longValue() );
		assertEquals( "Punch I", ti1.getMemo() );
	}

	@Test
	public void testSelectAllForTrade() {
		List<TradeItem> tradeItems = tradeItemDao.selectAll( Trade.builder().id( 9L ).build() );
		assertNotNull( tradeItems );
		assertEquals( 2, tradeItems.size() );

		TradeItem ti1 = tradeItems.get(0);
		TradeItem ti2 = tradeItems.get(1);

		assertEquals( 9L, ti1.getTradeId().longValue() );
		assertEquals( OFFER, ti1.getOfferRequire() );
		assertEquals( 1, ti1.getSeqno().intValue() );
		assertEquals( 1, ti1.getQuantity().intValue() );
		assertEquals( 1001L, ti1.getItemId().longValue() );
		assertEquals( null, ti1.getMemo() );

		assertEquals( 9L, ti2.getTradeId().longValue() );
		assertEquals( REQUIRE, ti2.getOfferRequire() );
		assertEquals( 1, ti2.getSeqno().intValue() );
		assertEquals( 40, ti2.getQuantity().intValue() );
		assertEquals( 1002L, ti2.getItemId().longValue() );
		assertEquals( null, ti2.getMemo() );
	}

	@Test
	public void testSelectAllForItem() {
		List<TradeItem> tradeItems = tradeItemDao.selectAll( Item.builder().id( 1001L ).build() );
		assertNotNull( tradeItems );
		assertEquals( 15, tradeItems.size() );
	}

	@Test
	public void testInsertOneSuccess() {
		Trade tradeKey = Trade.builder().id( 6L ).build();
		int count = tradeItemDao.selectAll( tradeKey ).size();

		TradeItem newItem = TradeItem.builder()
				.tradeId( 6L )
				.offerRequire( REQUIRE )
				.seqno( 2 )
				.quantity( 1 )
				.itemId( 1010L )
				.memo( "sweetener" )
				.build();
		TradeItem item = tradeItemDao.insertOne( newItem );
		assertNotNull( item );
		assertEquals( 6L, item.getTradeId().longValue() );
		assertEquals( count + 1, tradeItemDao.selectAll( tradeKey ).size() );
	}

	@Test
	public void testInsertOneIdFail() {
		TradeItem newItem = TradeItem.builder()
				.tradeId( 1L )
				.offerRequire( OFFER )
				.seqno( 1 )
				.quantity( 1 )
				.itemId( 1003L )
				.memo( "greedy" )
				.build();
		Exception e = assertThrows( DaoConstraintException.class, () -> tradeItemDao.insertOne( newItem ) );
		assertNotNull( e );
		assertEquals( "Dataset of type \"TradeItem\" does not allow duplicate values for field \"TradeId-OfferRequire-Seqno\"", e.getMessage() );
		logger.error( e.getMessage() );
	}

	@Test
	public void testInsertMany() {
		int count = tradeItemDao.selectAll().size();

		List<TradeItem> newItems = Arrays.asList(
			TradeItem.builder()
				.tradeId( 100L )
				.offerRequire( OFFER )
				.seqno( 1 )
				.quantity( 1 )
				.itemId( 1001L )
				.build()
			,
			TradeItem.builder()
				.tradeId( 100L )
				.offerRequire( REQUIRE )
				.seqno( 1 )
				.quantity( 10 )
				.itemId( 1010L )
				.build()
		);
		List<TradeItem> items = tradeItemDao.insert( newItems );
		assertNotNull( items );
		assertEquals( 2, items.size() );
		assertEquals( count + 2, tradeItemDao.selectAll().size() );
	}


	@Test
	public void testUpdateExisting() {
		List<TradeItem> tradeItems = tradeItemDao.selectAll( Trade.builder().id( 9L ).build() );
		TradeItem tradeItem = tradeItems.get(1);
		assertNotNull( tradeItem );

		int newQuantity = 35;
		tradeItem.setQuantity( newQuantity );
		TradeItem finalItem = tradeItemDao.update( tradeItem );

		assertNotNull( finalItem );
		assertEquals( newQuantity, finalItem.getQuantity().intValue() );
		assertEquals( tradeItems.size(), tradeItemDao.selectAll( Trade.builder().id( 9L ).build() ).size() );
	}

	@Test
	public void testUpdateFailNonExisting() {
		TradeItem item = TradeItem.builder()
				.tradeId( 1L )
				.offerRequire( OFFER )
				.seqno( 3 )
				.quantity( 10 )
				.build();
		TradeItem response = tradeItemDao.update( item );
		assertNull( response );

		item = TradeItem.builder()
				.tradeId( 100L )
				.offerRequire( OFFER )
				.seqno( 1 )
				.quantity( 10 )
				.build();
		response = tradeItemDao.update( item );
		assertNull( response );

		item = TradeItem.builder()
				.tradeId( 1L )
				.offerRequire( null )
				.seqno( 1 )
				.quantity( 10 )
				.build();
		response = tradeItemDao.update( item );
		assertNull( response );
	}

	@Test
	public void testDeleteSuccess() {
		int count = tradeItemDao.selectAll().size();

		List<TradeItem> tradeItems = tradeItemDao.selectAll( Trade.builder().id( 9L ).build() );
		TradeItem tradeItem = tradeItems.get(1);
		assertNotNull( tradeItem );

		boolean wasDeleted = tradeItemDao.deleteOne( tradeItem );
		assertTrue( wasDeleted );
		assertEquals( count - 1, tradeItemDao.selectAll().size() );
	}

}
