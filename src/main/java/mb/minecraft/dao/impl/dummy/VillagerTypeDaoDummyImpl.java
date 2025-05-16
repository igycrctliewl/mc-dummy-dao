package mb.minecraft.dao.impl.dummy;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import mb.minecraft.dao.DaoConstraintException;
import mb.minecraft.dao.ForeignKeyConstraintException;
import mb.minecraft.dao.VillagerDao;
import mb.minecraft.dao.VillagerTypeDao;
import mb.minecraft.model.Villager;
import mb.minecraft.model.VillagerType;

/**
 *
 * @author mikebro
 */
@Repository
public class VillagerTypeDaoDummyImpl implements VillagerTypeDao {

	private static final Logger logger = LogManager.getLogger( VillagerTypeDaoDummyImpl.class );

	@Autowired
	VillagerDao villagerDao;


	private Map<Long,VillagerType> villagerTypeTable;
	private long idSeq;


	@PostConstruct
	public void init() {
		logger.info( "VillagerTypeDaoDummyImpl.init()" );
	}


	private VillagerTypeDaoDummyImpl() {
		logger.info( "VillagerTypeDaoDummyImpl constructor" );
		this.villagerTypeTable = new HashMap<>();
		this.idSeq = 0L;
		for( VillagerType type : generateMockData() ) {
			insertOne( type );
		}
	}

	@Override
	public VillagerType selectOneById( Long id ) {
		return villagerTypeTable.get( id );
	}

	@Override
	public VillagerType selectOneByName( String name ) {
		Optional<VillagerType> optionalType = villagerTypeTable.values().stream()
				.filter( t -> t.getProfession().equals( name ) )
				.findFirst();
		if( optionalType.isPresent() )
			return optionalType.get();
		else
			return null;
	}

	@Override
	public List<VillagerType> selectAll() {
		return new ArrayList<>( villagerTypeTable.values() );
	}

	@Override
	public VillagerType insertOne( VillagerType newRow ) {
		forceEntityId( newRow, deriveId( newRow ) );
		testUniqueIdConstraint( newRow );
		testUniqueNameConstraint( newRow );
		villagerTypeTable.put( newRow.getId(), newRow );
		return newRow;
	}

	@Override
	public boolean deleteOne( VillagerType villagerType ) {
		// this method must make sure that villagerType is not currently
		// assigned to any Villager.
		List<Villager> villagers = villagerDao.selectAll().stream()
				.filter( v -> v.getTypeId() != null && v.getTypeId().equals( villagerType.getId() ) )
				.collect( Collectors.toList() );

		if( villagers.size() == 0 ) {
			if( villagerTypeTable.containsKey( villagerType.getId() ) ) {
				villagerTypeTable.remove( villagerType.getId() );
				return true;
			} else {
				return false;
			}
		} else {
			throw new ForeignKeyConstraintException(
					String.format( ForeignKeyConstraintException.FOREIGN_KEY_CONSTRAINT_ERROR, "VillagerType", villagerType.getId(), "Villager", villagers.get(0).getId() ) );

		}
	}

	@Override
	public void destroy() throws Exception {
		logger.info( "Shutting down VillagerTypeDaoDummyImpl" );
	}



	private long deriveId( VillagerType newRow ) {
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

	private void testUniqueIdConstraint( VillagerType row ) {
		if( villagerTypeTable.containsKey( row.getId() ) ) {
			throw new DaoConstraintException(
					String.format( DaoConstraintException.UNIQUE_CONSTRAINT_ERROR, "VillagerType", "ID" ), row );
		}
	}

	private void testUniqueNameConstraint( VillagerType row ) {
		VillagerType t = selectOneByName( row.getProfession() );
		if( t != null ) {
			throw new DaoConstraintException(
					String.format( DaoConstraintException.UNIQUE_CONSTRAINT_ERROR, "VillagerType", "PROFESSION" ), row );
		}
	}

	private void forceEntityId( VillagerType row, long id ) {
		try {
			Method setIdMethod = VillagerType.class.getDeclaredMethod( "setId", Long.class );
			setIdMethod.setAccessible( true );
			setIdMethod.invoke( row, id );
		} catch (NoSuchMethodException e) {
			logger.error( "NoSuchMethodException", e );
		} catch (SecurityException e) {
			logger.error( "SecurityException", e );
		} catch (IllegalAccessException e) {
			logger.error( "IllegalAccessException", e );
		} catch (IllegalArgumentException e) {
			logger.error( "IllegalArgumentException", e );
		} catch (InvocationTargetException e) {
			logger.error( "InvocationTargetException", e );
		}
	}



	private List<VillagerType> generateMockData() {
		List<VillagerType> list = new ArrayList<>();
		list.add( generateObject( 101L, "Librarian" ) );
		list.add( generateObject( 102L, "Leatherworker" ) );
		list.add( generateObject( 103L, "Shepherd" ) );
		list.add( generateObject( 104L, "Butcher" ) );
		list.add( generateObject( 105L, "Cleric" ) );
		return list;
	}

	private VillagerType generateObject( Long id, String profession ) {
		return VillagerType.builder()
				.id( id )
				.profession( profession )
				.build();
	}

}
