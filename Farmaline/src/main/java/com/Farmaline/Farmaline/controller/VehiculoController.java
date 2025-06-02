package com.Farmaline.Farmaline.controller;

import com.Farmaline.Farmaline.dto.VehiculoDTO;
import com.Farmaline.Farmaline.service.VehiculoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/vehiculos")
public class VehiculoController {

    private final VehiculoService vehiculoService;

    @Autowired
    public VehiculoController(VehiculoService vehiculoService) {
        this.vehiculoService = vehiculoService;
    }

    @GetMapping
    public ResponseEntity<List<VehiculoDTO>> getAllVehiculos() {
        List<VehiculoDTO> vehiculos = vehiculoService.findAllVehiculos();
        return new ResponseEntity<>(vehiculos, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<VehiculoDTO> getVehiculoById(@PathVariable Integer id) {
        Optional<VehiculoDTO> vehiculo = vehiculoService.findVehiculoById(id);
        return vehiculo.map(dto -> new ResponseEntity<>(dto, HttpStatus.OK))
                       .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping
    public ResponseEntity<VehiculoDTO> createVehiculo(@RequestBody VehiculoDTO vehiculoDTO) {
        try {
            VehiculoDTO newVehiculo = vehiculoService.createVehiculo(vehiculoDTO);
            return new ResponseEntity<>(newVehiculo, HttpStatus.CREATED);
        } catch (IllegalArgumentException | IllegalStateException e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<VehiculoDTO> updateVehiculo(@PathVariable Integer id, @RequestBody VehiculoDTO vehiculoDTO) {
        try {
            VehiculoDTO updatedVehiculo = vehiculoService.updateVehiculo(id, vehiculoDTO);
            return new ResponseEntity<>(updatedVehiculo, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND); 
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVehiculo(@PathVariable Integer id) {
        try {
            vehiculoService.deleteVehiculo(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT); 
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/search")
    public ResponseEntity<List<VehiculoDTO>> searchVehiculos(
            @RequestParam(required = false) String placa,
            @RequestParam(required = false) String categoria,
            @RequestParam(required = false) String marca,
            @RequestParam(required = false) String modelo,
            @RequestParam(required = false) Integer anio,
            @RequestParam(required = false) Integer anioMin) { 

        if (placa != null && !placa.isEmpty()) {
            Optional<VehiculoDTO> vehiculo = vehiculoService.findVehiculoByPlaca(placa);
            return vehiculo.map(dto -> ResponseEntity.ok(List.of(dto)))
                           .orElseGet(() -> ResponseEntity.ok(List.of())); 
        } else if (categoria != null && !categoria.isEmpty()) {
            return new ResponseEntity<>(vehiculoService.findVehiculosByMarca(categoria), HttpStatus.OK); 
        } else if (marca != null && !marca.isEmpty()) {
            return new ResponseEntity<>(vehiculoService.findVehiculosByMarca(marca), HttpStatus.OK);
        } else if (modelo != null && !modelo.isEmpty()) {
            return new ResponseEntity<>(vehiculoService.findByModeloContainingIgnoreCase(modelo), HttpStatus.OK);
        } else if (anio != null) {
            return new ResponseEntity<>(vehiculoService.findVehiculosByAnioGreaterThanEqual(anio), HttpStatus.OK);
        } else if (anioMin != null) {
            return new ResponseEntity<>(vehiculoService.findVehiculosByAnioGreaterThanEqual(anioMin), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(vehiculoService.findAllVehiculos(), HttpStatus.OK);
        }
    }
}