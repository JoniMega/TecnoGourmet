package com.example.demo.Persistence;

import com.example.demo.Domini.Restaurant;
import com.example.demo.Domini.Usuari;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;


import java.util.List;

@Repository
public class RestaurantDAO {

    private JdbcTemplate jdbcTemplate;

    private final String INSERT = "insert into Restaurant (nom, direccio, poblacio, puntuacio, descripcio, telefon) values(?, ?, ?, ?, ?, ?)";
    private final String FIND_ALL = "select * from Restaurant";
    private final String FIND_BY_RESTAURANT_NAME = "select * from Restaurant where nom = ?";
    private final String FIND_BY_POBLACIO = "select * from Restaurant where poblacio = ?";
    private final String FIND_BY_PUNTUACIO = "select * from Restaurant where puntuacio = ?";
    private final String UPDATE = "update Restaurant set nom = ?, direccio = ?, poblacio = ?, puntuacio = ?, descripcio= ?, telefon = ?";



    private final RowMapper<Restaurant> mapper = (resultSet, i) -> {
        return new Restaurant.RestaurantBuilder()
                .nomRestaurant(resultSet.getString("nom"))
                .direccio(resultSet.getString("direccio"))
                .poblacio(resultSet.getString("poblacio"))
                .puntuacio(resultSet.getInt("puntuacio"))
                .descripcio(resultSet.getString("descripcio"))
                .numTelefon(resultSet.getInt("telefon"))
                .build();
    };


    public  RestaurantDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Restaurant> findAll() {
        //instead of using the rowMapper it uses the BeanPropertyRowMapper to fo it authomatically
        return jdbcTemplate.query(FIND_ALL, mapper);
    }


    public int insert(Restaurant restaurant) {
        return jdbcTemplate.update(INSERT, restaurant.getNomRestaurant(), restaurant.getDireccio(), restaurant.getPoblacio(),
                restaurant.getPuntuacio(),restaurant.getDescripcio(),restaurant.getNumTelefon());
    }

    public int update(Restaurant restaurant){
        return jdbcTemplate.update(UPDATE,restaurant.getNomRestaurant(), restaurant.getDireccio(), restaurant.getPoblacio(),
                restaurant.getPuntuacio(),restaurant.getDescripcio(),restaurant.getNumTelefon());
    }

    public List<Restaurant> findByPoblacio(String poblacio) {
        //instead of using the rowMapper it uses the BeanPropertyRowMapper to fo it authomatically
        return jdbcTemplate.query(FIND_BY_POBLACIO, new Object[]{poblacio},mapper);
    }

    public Restaurant findByName(String nom) {
        return jdbcTemplate.queryForObject(FIND_BY_RESTAURANT_NAME, new Object[]{nom},mapper);
    }

    public List<Restaurant> findByPuntuacio(int puntuacio) {
        //instead of using the rowMapper it uses the BeanPropertyRowMapper to fo it authomatically
        return jdbcTemplate.query(FIND_BY_PUNTUACIO, new Object[]{puntuacio},mapper);
    }

}
