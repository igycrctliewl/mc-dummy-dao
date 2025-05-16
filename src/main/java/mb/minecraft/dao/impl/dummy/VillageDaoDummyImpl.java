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
import mb.minecraft.dao.VillageDao;
import mb.minecraft.model.Village;

/**
 *
 * @author mikebro
 */
@Repository
public class VillageDaoDummyImpl implements VillageDao {

	private static final Logger logger = LogManager.getLogger( VillageDaoDummyImpl.class );

	private Map<Long,Village> villageTable;
	private long idSeq;


	@PostConstruct
	public void init() {
		logger.info( "VillageDaoDummyImpl.init()" );
	}


	private VillageDaoDummyImpl() {
		logger.info( "VillageDaoDummyImpl constructor" );
		this.villageTable = new HashMap<>();
		this.idSeq = 0L;
		for( Village v : generateMockData() ) {
			insertOne( v );
		}
	}

	@Override
	public Village selectOneById( Long id ) {
		return villageTable.get( id );
	}

	@Override
	public Village selectOneByName( String name ) {
		Optional<Village> optionalVillage = villageTable.values().stream()
				.filter( v -> v.getName().equals( name ) )
				.findFirst();
		if( optionalVillage.isPresent() )
			return optionalVillage.get();
		else
			return null;
	}

	@Override
	public List<Village> selectAll() {
		return new ArrayList<>( villageTable.values() );
	}

	@Override
	public Village insertOne( Village newRow ) {
		newRow.setId( deriveId( newRow ) );
		testUniqueIdConstraint( newRow );
		testUniqueNameConstraint( newRow );
		villageTable.put( newRow.getId(), newRow );
		return newRow;
	}

	@Override
	public Village update( Village village ) {
		if( villageTable.containsKey( village.getId() ) ) {
			villageTable.put( village.getId(), village );
			return village;
		}
		return null;
	}

	@Override
	public boolean deleteOne( Village village ) {
		if( villageTable.containsKey( village.getId() ) ) {
			villageTable.remove( village.getId() );
			return true;
		} else {
			return false;
		}
	}

	@Override
	public void destroy() throws Exception {
		logger.info( "Shutting down VillageDaoDummyImpl" );
	}



	private long deriveId( Village newRow ) {
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

	private void testUniqueIdConstraint( Village row ) {
		if( villageTable.containsKey( row.getId() ) ) {
			throw new DaoConstraintException(
					String.format( DaoConstraintException.UNIQUE_CONSTRAINT_ERROR, "Village", "ID" ), row );
		}
	}

	private void testUniqueNameConstraint( Village row ) {
		Village v = this.selectOneByName( row.getName() );
		if( v != null ) {
			throw new DaoConstraintException(
					String.format( DaoConstraintException.UNIQUE_CONSTRAINT_ERROR, "Village", "NAME" ), row );
		}
	}



	private List<Village> generateMockData() {
		List<Village> list = new ArrayList<>();
		list.add( generateObject( 201L, "Deep Water Cove" ) );
		list.add( generateObject( 202L, "Heart" ) );
		list.add( generateObject( 203L, "Giant Oaks" ) );
		list.add( generateObject( 204L, "Kingdom of the Southern Islands" ) );
		list.add( generateObject( 205L, "Winter Village" ) );
		list.add( generateObject( 211L, "Lathlain" ) );
		list.add( generateObject( 212L, "Geelong" ) );
		list.add( generateObject( 213L, "Calgary" ) );
		list.add( generateObject( 214L, "Colorado Springs" ) );
		list.add( generateObject( 215L, "Newark" ) );
		return list;
	}

	private Village generateObject( Long id, String name ) {
		return Village.builder()
				.id( id )
				.name( name )
				.build();
	}


}
