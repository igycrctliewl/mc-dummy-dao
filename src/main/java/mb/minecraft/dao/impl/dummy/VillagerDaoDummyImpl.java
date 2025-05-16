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
import mb.minecraft.dao.VillagerDao;
import mb.minecraft.model.Villager;

/**
 *
 * @author mikebro
 */
@Repository
public class VillagerDaoDummyImpl implements VillagerDao {

	private static final Logger logger = LogManager.getLogger( VillagerDaoDummyImpl.class );

	private Map<Long,Villager> villagerTable;
	private long idSeq;


	@PostConstruct
	public void init() {
		logger.info( "VillagerDaoDummyImpl.init()" );
	}


	private VillagerDaoDummyImpl() {
		logger.info( "VillagerDaoDummyImpl constructor" );
		this.villagerTable = new HashMap<>();
		this.idSeq = 0L;
		for( Villager v : generateMockData() ) {
			insertOne( v );
		}
	}

	@Override
	public Villager selectOneById( Long id ) {
		return villagerTable.get( id );
	}

	@Override
	public Villager selectOneByName( String name ) {
		Optional<Villager> optionalVillager = villagerTable.values().stream()
				.filter( v -> v.getName().equals( name ) )
				.findFirst();
		if( optionalVillager.isPresent() )
			return optionalVillager.get();
		else
			return null;
	}

	@Override
	public List<Villager> selectAll() {
		return new ArrayList<>( villagerTable.values() );
	}

	@Override
	public Villager insertOne( Villager newRow ) {
		newRow.setId( deriveId( newRow ) );
		testUniqueIdConstraint( newRow );
		testUniqueNameConstraint( newRow );
		villagerTable.put( newRow.getId(), newRow );
		return newRow;
	}

	@Override
	public Villager update( Villager villager ) {
		if( villagerTable.containsKey( villager.getId() ) ) {
			villagerTable.put( villager.getId(), villager );
			return villager;
		}
		return null;
	}

	@Override
	public boolean deleteOne( Villager villager ) {
		if( villagerTable.containsKey( villager.getId() ) ) {
			villagerTable.remove( villager.getId() );
			return true;
		} else {
			return false;
		}
	}

	@Override
	public void destroy() throws Exception {
		logger.info( "Shutting down VillagerDaoDummyImpl" );
	}



	private long deriveId( Villager newRow ) {
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

	private void testUniqueIdConstraint( Villager row ) {
		if( villagerTable.containsKey( row.getId() ) ) {
			throw new DaoConstraintException(
					String.format( DaoConstraintException.UNIQUE_CONSTRAINT_ERROR, "Villager", "ID" ), row );
		}
	}

	private void testUniqueNameConstraint( Villager row ) {
		Villager v = selectOneByName( row.getName() );
		if( v != null ) {
			throw new DaoConstraintException(
					String.format( DaoConstraintException.UNIQUE_CONSTRAINT_ERROR, "Villager", "NAME" ), row );
		}
	}



	private List<Villager> generateMockData() {
		List<Villager> list = new ArrayList<>();
		list.add( generateObject( 699L, "Liam Z", true, 201L, 105L ) );
		list.add( generateObject( 701L, "Gary", false, 214L, null ) );
		list.add( generateObject( 702L, "Malcolm", false, 215L, null ) );
		list.add( generateObject( 73L, "Tyler", false, 213L, null ) );
		list.add( generateObject( 704L, "Dana", false, 211L, null ) );
		list.add( generateObject( 705L, "Amy", false, 212L, null ) );
		return list;
	}

	private Villager generateObject( Long id, String name, boolean isTagged, Long villageId, Long typeId ) {
		return Villager.builder()
				.id( id )
				.name( name )
				.tagged( isTagged )
				.villageId( villageId )
				.typeId( typeId )
				.build();
	}

}
