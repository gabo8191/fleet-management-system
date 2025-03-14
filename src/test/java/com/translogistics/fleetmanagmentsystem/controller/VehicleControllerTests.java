//package com.translogistics.fleetmanagmentsystem.controller;
//
//import com.translogistics.fleetmanagmentsystem.dto.MaintenanceDto;
//import com.translogistics.fleetmanagmentsystem.dto.VehicleDto;
//import com.translogistics.fleetmanagmentsystem.enums.VehicleStatus;
//import com.translogistics.fleetmanagmentsystem.enums.VehicleType;
//import com.translogistics.fleetmanagmentsystem.model.Vehicle;
//import com.translogistics.fleetmanagmentsystem.service.DriverService;
//import com.translogistics.fleetmanagmentsystem.service.MaintenanceService;
//import com.translogistics.fleetmanagmentsystem.service.VehicleService;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.setup.MockMvcBuilders;
//import org.springframework.ui.Model;
//import org.springframework.validation.BindingResult;
//
//import java.time.LocalDateTime;
//import java.util.Arrays;
//import java.util.Optional;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.mockito.ArgumentMatchers.*;
//import static org.mockito.Mockito.*;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//
//@ExtendWith(MockitoExtension.class)
//public class VehicleControllerTests {
//
//    @Mock
//    private VehicleService vehicleService;
//
//    @Mock
//    private DriverService driverService;
//
//    @Mock
//    private MaintenanceService maintenanceService;
//
//    @Mock
//    private Model model;
//
//    @Mock
//    private BindingResult bindingResult;
//
//    @InjectMocks
//    private VehicleController vehicleController;
//
//    private MockMvc mockMvc;
//    private Vehicle testVehicle;
//    private VehicleDto testVehicleDto;
//
//    @BeforeEach
//    void setUp() {
//        mockMvc = MockMvcBuilders.standaloneSetup(vehicleController).build();
//
//        // Configuración de un vehículo de prueba
//        testVehicle = new Vehicle();
//        testVehicle.setId(1L);
//        testVehicle.setLicensePlate("ABC123");
//        testVehicle.setModel("Modelo X");
//        testVehicle.setYear(2020);
//        testVehicle.setType(VehicleType.CARGA);
//        // Actualizado a los valores válidos del enum VehicleStatus
//        testVehicle.setStatus(VehicleStatus.ACTIVO);
//
//        // Configuración de un DTO para crear/actualizar vehículo
//        testVehicleDto = new VehicleDto();
//        testVehicleDto.setLicensePlate("XYZ789");
//        testVehicleDto.setModel("Modelo Y");
//        testVehicleDto.setYear(2021);
//        testVehicleDto.setType(VehicleType.PASAJEROS);
//        // Actualizado a los valores válidos del enum VehicleStatus
//        testVehicleDto.setStatus(VehicleStatus.MANTEMINIENTO);
//    }
//
//    // 1. Listar vehículos
//    @Test
//    void listVehiclesShouldReturnVehiclesListView() throws Exception {
//        when(vehicleService.findAllVehicles()).thenReturn(Arrays.asList(testVehicle));
//
//        mockMvc.perform(get("/vehicles"))
//                .andExpect(status().isOk())
//                .andExpect(model().attributeExists("vehicles"))
//                .andExpect(view().name("vehicles/list"));
//
//        verify(vehicleService, times(1)).findAllVehicles();
//    }
//
//    // 2. Mostrar formulario de creación
//    @Test
//    void showCreateFormShouldReturnCreateView() throws Exception {
//        mockMvc.perform(get("/vehicles/create"))
//                .andExpect(status().isOk())
//                .andExpect(model().attributeExists("vehicle"))
//                .andExpect(model().attributeExists("vehicleTypes"))
//                .andExpect(model().attributeExists("vehicleStatuses"))
//                .andExpect(view().name("vehicles/create"));
//    }
//
//    // 3. Crear vehículo: errores de validación
//    @Test
//    void createVehicleShouldReturnCreateViewWhenValidationErrors() {
//        when(bindingResult.hasErrors()).thenReturn(true);
//
//        String viewName = vehicleController.createVehicle(testVehicleDto, bindingResult, model);
//        // Se vuelven a agregar los atributos en el controlador
//        verify(model, times(1)).addAttribute(eq("vehicleTypes"), any());
//        verify(model, times(1)).addAttribute(eq("vehicleStatuses"), any());
//        assertEquals("vehicles/create", viewName);
//        verify(vehicleService, never()).createVehicle(any(VehicleDto.class));
//    }
//
//    // 4. Crear vehículo: placa ya existe
//    @Test
//    void createVehicleShouldReturnCreateViewWhenLicensePlateExists() {
//        when(bindingResult.hasErrors()).thenReturn(false);
//        when(vehicleService.licensePlateExists(testVehicleDto.getLicensePlate())).thenReturn(true);
//
////        String viewName = vehicleController.createVehicle(testVehicleDto, bindingResult, model);
//        verify(bindingResult, times(1)).rejectValue(eq("licensePlate"), eq("error.vehicle"), anyString());
//        verify(model, times(1)).addAttribute(eq("vehicleTypes"), any());
//        verify(model, times(1)).addAttribute(eq("vehicleStatuses"), any());
////        assertEquals("vehicles/create", viewName);
//        verify(vehicleService, never()).createVehicle(any(VehicleDto.class));
//    }
//
//    // 5. Crear vehículo: caso exitoso
//    @Test
//    void createVehicleShouldRedirectWhenValid() {
//        when(bindingResult.hasErrors()).thenReturn(false);
//        when(vehicleService.licensePlateExists(testVehicleDto.getLicensePlate())).thenReturn(false);
//        doNothing().when(vehicleService).createVehicle(testVehicleDto);
//
//        String viewName = vehicleController.createVehicle(testVehicleDto, bindingResult, model);
//        assertEquals("redirect:/vehicles", viewName);
//        verify(vehicleService, times(1)).createVehicle(testVehicleDto);
//    }
//
//    // 6. Mostrar formulario de edición cuando el vehículo existe
//    @Test
//    void showEditFormShouldReturnEditViewWhenVehicleExists() throws Exception {
//        when(vehicleService.findById(1L)).thenReturn(Optional.of(testVehicle));
//        when(driverService.findAllDrivers()).thenReturn(Arrays.asList());
//
//        mockMvc.perform(get("/vehicles/edit/1"))
//                .andExpect(status().isOk())
//                .andExpect(model().attributeExists("vehicle"))
//                .andExpect(model().attributeExists("vehicleId"))
//                .andExpect(model().attributeExists("drivers"))
//                .andExpect(model().attributeExists("vehicleTypes"))
//                .andExpect(model().attributeExists("vehicleStatuses"))
//                .andExpect(view().name("vehicles/edit"));
//
//        verify(vehicleService, times(1)).findById(1L);
//        verify(driverService, times(1)).findAllDrivers();
//    }
//
//    // 7. Mostrar formulario de edición cuando el vehículo no existe
//    @Test
//    void showEditFormShouldThrowExceptionWhenVehicleNotFound() throws Exception {
//        when(vehicleService.findById(999L)).thenReturn(Optional.empty());
//
//        try {
//            mockMvc.perform(get("/vehicles/edit/999"));
//        } catch (Exception e) {
//            assertEquals("Vehículo no encontrado", e.getCause().getMessage());
//        }
//        verify(vehicleService, times(1)).findById(999L);
//    }
//
//    // 8. Actualizar vehículo: errores de validación
//    @Test
//    void updateVehicleShouldReturnEditViewWhenValidationErrors() {
//        when(bindingResult.hasErrors()).thenReturn(true);
//
//        String viewName = vehicleController.updateVehicle(1L, testVehicleDto, bindingResult, model);
//        verify(model, times(1)).addAttribute(eq("vehicleId"), eq(1L));
//        verify(model, times(1)).addAttribute(eq("vehicleTypes"), any());
//        verify(model, times(1)).addAttribute(eq("vehicleStatuses"), any());
//        assertEquals("vehicles/edit", viewName);
//        verify(vehicleService, never()).updateVehicle(anyLong(), any(VehicleDto.class));
//    }
//
//    // 9. Actualizar vehículo: caso exitoso
//    @Test
//    void updateVehicleShouldRedirectWhenValid() {
//        when(bindingResult.hasErrors()).thenReturn(false);
//        doNothing().when(vehicleService).updateVehicle(1L, testVehicleDto);
//
//        String viewName = vehicleController.updateVehicle(1L, testVehicleDto, bindingResult, model);
//        assertEquals("redirect:/vehicles", viewName);
//        verify(vehicleService, times(1)).updateVehicle(1L, testVehicleDto);
//    }
//
//    // 10. Alternar estado del vehículo
//    @Test
//    void toggleVehicleStatusShouldRedirectToList() throws Exception {
//        doNothing().when(vehicleService).toggleVehicleStatus(1L);
//
//        mockMvc.perform(get("/vehicles/toggle-status/1"))
//                .andExpect(status().is3xxRedirection())
//                .andExpect(redirectedUrl("/vehicles"));
//
//        verify(vehicleService, times(1)).toggleVehicleStatus(1L);
//    }
//
//    // 11. Asignar conductor a vehículo
//    @Test
//    void assignDriverShouldRedirectToList() throws Exception {
//        // Se simula la asignación, sin necesidad de retorno
//        doNothing().when(vehicleService).assignDriver(eq(1L), eq(2L), any(LocalDateTime.class));
//
//        mockMvc.perform(post("/vehicles/assign-driver/1")
//                        .param("driverId", "2")
//                        .param("assignmentStart", LocalDateTime.now().plusDays(1).toString()))
//                .andExpect(status().is3xxRedirection())
//                .andExpect(redirectedUrl("/vehicles"));
//
//        verify(vehicleService, times(1)).assignDriver(eq(1L), eq(2L), any(LocalDateTime.class));
//    }
//
//    // 12. Ver historial de mantenimientos
//    @Test
//    void viewMaintenanceHistoryShouldReturnMaintenanceHistoryView() throws Exception {
//        when(vehicleService.findById(1L)).thenReturn(Optional.of(testVehicle));
//        when(maintenanceService.findMaintenancesByVehicleId(1L)).thenReturn(Arrays.asList());
//
//        mockMvc.perform(get("/vehicles/1/maintenances"))
//                .andExpect(status().isOk())
//                .andExpect(model().attributeExists("vehicle"))
//                .andExpect(model().attributeExists("maintenances"))
//                .andExpect(view().name("vehicles/maintenance-history"));
//
//        verify(vehicleService, times(1)).findById(1L);
//        verify(maintenanceService, times(1)).findMaintenancesByVehicleId(1L);
//    }
//
//    // 13. Mostrar formulario para programar mantenimiento
//    @Test
//    void showScheduleMaintenanceFormShouldReturnScheduleMaintenanceView() throws Exception {
//        mockMvc.perform(get("/vehicles/1/schedule-maintenance"))
//                .andExpect(status().isOk())
//                .andExpect(model().attributeExists("vehicleId"))
//                .andExpect(model().attributeExists("maintenance"))
//                .andExpect(view().name("vehicles/schedule-maintenance"));
//    }
//
//
//}
//
