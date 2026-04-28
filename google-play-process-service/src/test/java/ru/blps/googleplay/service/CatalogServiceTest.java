package ru.blps.googleplay.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.blps.googleplay.dto.AppItemRequest;
import ru.blps.googleplay.dto.AppItemResponse;
import ru.blps.googleplay.entity.AppItem;
import ru.blps.googleplay.exception.NotFoundException;
import ru.blps.googleplay.repository.AppItemRepository;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CatalogServiceTest {

    @Mock
    private AppItemRepository appItemRepository;

    @InjectMocks
    private CatalogService catalogService;

    @Test
    void updateChangesActiveAppFields() {
        AppItem app = appItem(1L, "com.example.old", "Old", "Old description", "0.00");
        AppItemRequest request = request("com.example.new", "New", "New description", "1.99");

        when(appItemRepository.findByIdAndActiveTrue(1L)).thenReturn(Optional.of(app));
        when(appItemRepository.save(app)).thenReturn(app);

        AppItemResponse response = catalogService.update(1L, request);

        assertEquals("com.example.new", response.getPackageName());
        assertEquals("New", response.getTitle());
        assertEquals("New description", response.getDescription());
        assertEquals(new BigDecimal("1.99"), response.getPrice());
        verify(appItemRepository).save(app);
    }

    @Test
    void deleteMarksAppInactive() {
        AppItem app = appItem(1L, "com.example.app", "App", "Description", "0.00");
        when(appItemRepository.findByIdAndActiveTrue(1L)).thenReturn(Optional.of(app));

        catalogService.delete(1L);

        assertFalse(app.isActive());
        verify(appItemRepository).save(app);
    }

    @Test
    void deletedAppIsNotFound() {
        when(appItemRepository.findByIdAndActiveTrue(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> catalogService.getById(1L));
    }

    private AppItem appItem(Long id, String packageName, String title, String description, String price) {
        AppItem app = new AppItem();
        app.setId(id);
        app.setPackageName(packageName);
        app.setTitle(title);
        app.setDescription(description);
        app.setPrice(new BigDecimal(price));
        app.setActive(true);
        return app;
    }

    private AppItemRequest request(String packageName, String title, String description, String price) {
        AppItemRequest request = new AppItemRequest();
        request.setPackageName(packageName);
        request.setTitle(title);
        request.setDescription(description);
        request.setPrice(new BigDecimal(price));
        return request;
    }
}
