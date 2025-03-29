package authstream.infrastructure.repositories;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import authstream.domain.entities.Route;
import jakarta.transaction.Transactional;

@Repository
public interface RouteRepository extends JpaRepository<Route, UUID> {

    String getAllRoutesQuery = "SELECT * FROM routes";
    String getRouteByIdQuery = "SELECT * FROM routes WHERE id = :id";
    String addRouteQuery = "INSERT INTO routes (id, name, route, method, protected, description, created_at, updated_at) " +
                           "VALUES (:id, :name, :route, :method, :protected, :description, :createdAt, :updatedAt)";
    String updateRouteQuery = "UPDATE routes SET name = :name, route = :route, method = :method, protected = :protected, " +
                              "description = :description, updated_at = :updatedAt WHERE id = :id";
    String deleteRouteQuery = "DELETE FROM routes WHERE id = :id";
    String selectRouteQuery = "SELECT * FROM routes WHERE route = :route";

    @Query(value = selectRouteQuery, nativeQuery = true)
    List<Route> findRouteByRoute(@Param("route") String route);

    @Query(value = getAllRoutesQuery, nativeQuery = true)
    List<Route> findAllRoutes();

    @Query(value = getRouteByIdQuery, nativeQuery = true)
    Route findRouteById(@Param("id") UUID id);

    @Modifying
    @Transactional
    @Query(value = addRouteQuery, nativeQuery = true)
    int addRoute(
            @Param("id") UUID id,
            @Param("name") String name,
            @Param("route") String route,
            @Param("method") String method,
            @Param("protected") Boolean checkProtected,
            @Param("description") String descripString,
            @Param("createdAt") LocalDateTime createdAt,
            @Param("updatedAt") LocalDateTime updatedAt);

    @Modifying
    @Transactional
    @Query(value = updateRouteQuery, nativeQuery = true)
    int updateRoute(
            @Param("id") UUID id,
            @Param("name") String name,
            @Param("route") String route,
            @Param("method") String method,
            @Param("protected") Boolean checkProtected,
            @Param("description") String descripString,
            @Param("updatedAt") LocalDateTime updatedAt);

    @Modifying
    @Transactional
    @Query(value = deleteRouteQuery, nativeQuery = true)
    int deleteRoute(@Param("id") UUID id);
}