package com.omnie.model.mongo.dao.impl;

import com.google.inject.Inject;
import com.omnie.model.KunderaEntityManageFactory;
import com.omnie.model.mongo.dao.GenericDao;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import java.lang.reflect.ParameterizedType;
import java.util.List;

/**
 * Created by Harmeet Singh(Taara) on 27/12/16.
 */
public abstract class GenericDaoImpl< E > implements GenericDao< E > {

	private Class< E > entityClass;

	private KunderaEntityManageFactory entityManageFactory;

	@Inject
	public GenericDaoImpl( KunderaEntityManageFactory entityManageFactory ) {
		EntityManagerFactory managerFactory = entityManageFactory.getManagerFactory( );
		ParameterizedType parameterizedType = ( ParameterizedType ) getClass( ).getGenericSuperclass( );
		entityClass = ( Class< E > ) parameterizedType.getActualTypeArguments( )[ 0 ];
	}

	private EntityManager getEntityManager( ) {
		return this.entityManageFactory.getManagerFactory( ).createEntityManager( );
	}

	@Override
	public E save( E entity ) {
		EntityManager em = getEntityManager( );
		EntityTransaction transaction = em.getTransaction( );
		transaction.begin( );
		em.persist( entity );
		transaction.commit( );
		return entity;
	}


	@Override
	public E findById( String objectId ) {
		EntityManager em = getEntityManager( );
		EntityTransaction transaction = em.getTransaction( );
		transaction.begin( );
		E entity = em.createQuery(
				"SELECT entity FROM " + entityClass.getSimpleName( ) + " entity WHERE entity.imageId = :id",
				entityClass )
				.setParameter( "id", objectId ).getSingleResult( );
		transaction.commit();
		return entity;
	}

	@Override
	public List< E > findAll( ) {
		EntityManager em = getEntityManager( );
		EntityTransaction transaction = em.getTransaction( );
		transaction.begin( );
				List<E> entities =  em.createQuery( "SELECT entity FROM " + entityClass.getSimpleName( ) + " entity", entityClass )
				.getResultList( );
		transaction.commit();
		return entities;
	}

	@Override
	public void delete( E entity ) {
		EntityManager em = getEntityManager( );
		EntityTransaction transaction = em.getTransaction( );
		transaction.begin( );
		em.remove( entity );
		transaction.commit( );
	}

	@Override
	public void delete( String objectId ) {
		EntityManager em = getEntityManager( );
		EntityTransaction transaction = em.getTransaction( );
		transaction.begin( );
		em.createQuery( "DELETE FROM " + entityClass.getSimpleName( ) + " entity WHERE entity.imageId = :id" )
				.setParameter( "id", objectId ).executeUpdate( );
		transaction.commit( );
	}


	@Override public void deleteByObjectId( List< String > objectIds ) {
		EntityManager em = getEntityManager( );
		EntityTransaction transaction = em.getTransaction( );
		transaction.begin( );
		em.createQuery( "DELETE FROM " + entityClass.getSimpleName( ) + " entity WHERE" +
				                " entity.imageId = :id" )
				.setParameter( "id", objectIds ).executeUpdate( );
		transaction.commit( );
	}


}
