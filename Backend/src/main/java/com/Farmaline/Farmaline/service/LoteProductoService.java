package com.farmaline.farmaline.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.farmaline.farmaline.dto.LoteProductoDTO;
import com.farmaline.farmaline.model.LoteProducto;
import com.farmaline.farmaline.model.Producto;
import com.farmaline.farmaline.repository.LoteProductoRepository;
import com.farmaline.farmaline.repository.ProductoRepository;

@Service
public class LoteProductoService {

    @Autowired
    private LoteProductoRepository loteProductoRepository;

    @Autowired
    private ProductoRepository productoRepository;

    public List<LoteProductoDTO> getAllLotesProducto() {
        return loteProductoRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public Optional<LoteProductoDTO> getLoteProductoById(Integer id) {
        return loteProductoRepository.findById(id)
                .map(this::convertToDTO);
    }

    public List<LoteProductoDTO> getLotesByProductId(Integer idProducto) {
        return loteProductoRepository.findByProducto_IdProductoOrderByFechaCaducidadAsc(idProducto).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public LoteProductoDTO createLoteProducto(LoteProductoDTO loteProductoDTO) {
        Producto producto = productoRepository.findById(loteProductoDTO.getIdProducto())
                .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado con ID: " + loteProductoDTO.getIdProducto()));

        if (loteProductoRepository.findByProducto_IdProductoAndNumeroLote(producto.getIdProducto(), loteProductoDTO.getNumeroLote()).isPresent()) {
            throw new IllegalArgumentException("Ya existe un lote con el mismo número para este producto.");
        }

        LoteProducto loteProducto = convertToEntity(loteProductoDTO);
        loteProducto.setProducto(producto);
        loteProducto.setFechaIngreso(LocalDate.now());

        LoteProducto savedLote = loteProductoRepository.save(loteProducto);
        return convertToDTO(savedLote);
    }

    @Transactional
    public Optional<LoteProductoDTO> updateLoteProducto(Integer idLoteProducto, LoteProductoDTO loteProductoDTO) {
        return loteProductoRepository.findById(idLoteProducto).map(existingLote -> {
            if (!existingLote.getNumeroLote().equalsIgnoreCase(loteProductoDTO.getNumeroLote()) &&
                loteProductoRepository.findByProducto_IdProductoAndNumeroLote(existingLote.getProducto().getIdProducto(), loteProductoDTO.getNumeroLote()).isPresent()) {
                throw new IllegalArgumentException("Ya existe otro lote con el mismo número para este producto.");
            }

            existingLote.setNumeroLote(loteProductoDTO.getNumeroLote());
            existingLote.setFechaCaducidad(loteProductoDTO.getFechaCaducidad());
            existingLote.setCantidadDisponible(loteProductoDTO.getCantidadDisponible());

            LoteProducto updatedLote = loteProductoRepository.save(existingLote);
            return convertToDTO(updatedLote);
        });
    }

    @Transactional
    public boolean deleteLoteProducto(Integer id) {
        if (loteProductoRepository.existsById(id)) {
            loteProductoRepository.deleteById(id);
            return true;
        }
        return false;
    }

    @Transactional
    public void consumeStockByExpiration(Integer idProducto, int cantidadRequerida) {
        if (cantidadRequerida <= 0) {
            throw new IllegalArgumentException("La cantidad a consumir debe ser mayor que cero.");
        }

        List<LoteProducto> lotes = loteProductoRepository.findByProducto_IdProductoOrderByFechaCaducidadAsc(idProducto);

        int totalStockDisponible = lotes.stream().mapToInt(LoteProducto::getCantidadDisponible).sum();

        if (totalStockDisponible < cantidadRequerida) {
            throw new IllegalStateException("Stock insuficiente para el producto ID " + idProducto + ". Disponible: " + totalStockDisponible + ", Requerido: " + cantidadRequerida);
        }

        int cantidadRestante = cantidadRequerida;
        for (LoteProducto lote : lotes) {
            if (cantidadRestante <= 0) {
                break;
            }

            int cantidadTomarDeLote = Math.min(cantidadRestante, lote.getCantidadDisponible());
            lote.setCantidadDisponible(lote.getCantidadDisponible() - cantidadTomarDeLote);
            loteProductoRepository.save(lote);

            cantidadRestante -= cantidadTomarDeLote;
        }
    }

    @Transactional
    public boolean increaseStock(Integer idLoteProducto, int cantidad) {
        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad a aumentar debe ser mayor que cero.");
        }
        return loteProductoRepository.findById(idLoteProducto).map(lote -> {
            lote.setCantidadDisponible(lote.getCantidadDisponible() + cantidad);
            loteProductoRepository.save(lote);
            return true;
        }).orElse(false);
    }

    public int getTotalStockByProductId(Integer idProducto) {
        return loteProductoRepository.findByProducto_IdProductoOrderByFechaCaducidadAsc(idProducto).stream()
                .mapToInt(LoteProducto::getCantidadDisponible)
                .sum();
    }

    @Transactional
    public int deleteExpiredLotes() {
        LocalDate today = LocalDate.now();
        List<LoteProducto> expiredLotes = loteProductoRepository.findByFechaCaducidadBefore(today);
        int count = expiredLotes.size();
        if (count > 0) {
            loteProductoRepository.deleteAll(expiredLotes);
        }
        return count;
    }

    @Transactional
    public void deleteByProducto_IdProducto(Integer idProducto) {
        loteProductoRepository.deleteByProducto_IdProducto(idProducto);
    }

    private LoteProductoDTO convertToDTO(LoteProducto loteProducto) {
        LoteProductoDTO dto = new LoteProductoDTO();
        dto.setIdLoteProducto(loteProducto.getIdLoteProducto());
        dto.setIdProducto(loteProducto.getProducto() != null ? loteProducto.getProducto().getIdProducto() : null);
        dto.setNumeroLote(loteProducto.getNumeroLote());
        dto.setFechaCaducidad(loteProducto.getFechaCaducidad());
        dto.setCantidadDisponible(loteProducto.getCantidadDisponible());
        dto.setFechaIngreso(loteProducto.getFechaIngreso());
        return dto;
    }

    private LoteProducto convertToEntity(LoteProductoDTO loteProductoDTO) {
        LoteProducto loteProducto = new LoteProducto();
        loteProducto.setIdLoteProducto(loteProductoDTO.getIdLoteProducto());
        loteProducto.setNumeroLote(loteProductoDTO.getNumeroLote());
        loteProducto.setFechaCaducidad(loteProductoDTO.getFechaCaducidad());
        loteProducto.setCantidadDisponible(loteProductoDTO.getCantidadDisponible());
        loteProducto.setFechaIngreso(loteProductoDTO.getFechaIngreso());
        return loteProducto;
    }
}