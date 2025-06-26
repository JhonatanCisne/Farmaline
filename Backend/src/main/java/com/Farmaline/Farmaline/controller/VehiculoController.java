package com.farmaline.farmaline.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.farmaline.farmaline.dto.VehiculoDTO;
import com.farmaline.farmaline.service.VehiculoService;

@RestController
@RequestMapping("/api/vehiculos")
public class VehiculoController {

    private final VehiculoService vehiculoService;

    public VehiculoController(VehiculoService vehiculoService) {
        this.vehiculoService = vehiculoService;
    }

    @GetMapping
    public ResponseEntity<List<VehiculoDTO>> obtenerTodosVehiculos() {
        List<VehiculoDTO> vehiculos = vehiculoService.obtenerTodosVehiculos();
        return new ResponseEntity<>(vehiculos, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<VehiculoDTO> obtenerVehiculoPorId(@PathVariable Integer id) {
        return vehiculoService.obtenerVehiculoPorId(id)
                .map(vehiculoDTO -> new ResponseEntity<>(vehiculoDTO, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping
    public ResponseEntity<VehiculoDTO> crearVehiculo(@RequestBody VehiculoDTO vehiculoDTO) {
        VehiculoDTO nuevoVehiculo = vehiculoService.crearVehiculo(vehiculoDTO);
        return new ResponseEntity<>(nuevoVehiculo, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<VehiculoDTO> actualizarVehiculo(@PathVariable Integer id, @RequestBody VehiculoDTO vehiculoDTO) {
        return vehiculoService.actualizarVehiculo(id, vehiculoDTO)
                .map(vehiculoActualizado -> new ResponseEntity<>(vehiculoActualizado, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarVehiculo(@PathVariable Integer id) {
        if (vehiculoService.eliminarVehiculo(id)) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}