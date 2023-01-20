public interface IRepository<E, Eid> {

    /**
     * add an entity
     * @param elem the entity will be added
     */
    void add(E elem);

    /**
     * remove an entity
     * @param elem the entity will be deleted
     */
    void delete(E elem);

    /**
     * update an entity
     * @param elem new entity
     * @param id the id for the entity which be updated
     */
    void update(E elem, Eid id);

    /**
     * @param id -the id of the entity to be returned
     * @return the entity with the specified id
     */
    E findById(Eid id);

    /**
     * @return all entities
     */
    Iterable<E> findAll();
}