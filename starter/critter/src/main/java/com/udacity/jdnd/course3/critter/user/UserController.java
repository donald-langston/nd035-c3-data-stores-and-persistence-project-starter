package com.udacity.jdnd.course3.critter.user;

import com.udacity.jdnd.course3.critter.pet.Pet;
import com.udacity.jdnd.course3.critter.pet.PetDTO;
import com.udacity.jdnd.course3.critter.service.UserService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Handles web requests related to Users.
 *
 * Includes requests for both customers and employees. Splitting this into separate user and customer controllers
 * would be fine too, though that is not part of the required scope for this class.
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    UserService userService;

    @PostMapping("/customer")
    public CustomerDTO saveCustomer(@RequestBody CustomerDTO customerDTO){
        Customer customer = userService.saveCustomer(convertCustomerDTOToEntity(customerDTO));
        return convertCustomerToCustomerDTO(customer);
    }

    @GetMapping("/customer")
    public List<CustomerDTO> getAllCustomers(){
        List<Customer> customers = userService.getCustomers();
        List<CustomerDTO> customersDto = new ArrayList<>();
        customers.forEach(customer -> customersDto.add(convertCustomerToCustomerDTO(customer)));
        return customersDto;
    }

    @GetMapping("/customer/pet/{petId}")
    public CustomerDTO getOwnerByPet(@PathVariable long petId){
        Customer customer = userService.getCustomerByPet(petId);
        return convertCustomerToCustomerDTO(customer);
    }

    @PutMapping("/customer/pet/{petId}")
    public CustomerDTO setPetsByOwner(@PathVariable long petId) {
        Customer customer = userService.updatePetsByCustomer(petId);
        return convertCustomerToCustomerDTO(customer);
    }

    @PostMapping("/employee")
    public EmployeeDTO saveEmployee(@RequestBody EmployeeDTO employeeDTO) {

        Employee employee = userService.saveEmployee(convertEmployeeDTOToEntity(employeeDTO));
        return convertEmployeeToEmployeeDTO(employee);
    }

    @GetMapping("/employee/{employeeId}")
    public EmployeeDTO getEmployee(@PathVariable long employeeId) {
        Employee employee = userService.getEmployee(employeeId);
        return convertEmployeeToEmployeeDTO(employee);
    }

    @GetMapping("/employee")
    public List<EmployeeDTO> getEmployees() {
        List<Employee> employees = userService.getEmployees();
        List<EmployeeDTO> employeesDto = new ArrayList<>();
        employees.forEach(employee -> employeesDto.add(convertEmployeeToEmployeeDTO(employee)));
        return employeesDto;
    }

    @PutMapping("/employee/{employeeId}")
    public void setAvailability(@RequestBody Set<DayOfWeek> daysAvailable, @PathVariable long employeeId) {
        userService.setAvailability(daysAvailable, employeeId);
    }

    @GetMapping("/employee/availability")
    public List<EmployeeDTO> findEmployeesForService(@RequestBody EmployeeRequestDTO employeeDTO) {
        List<Employee> employees = userService.findEmployeesForService(employeeDTO);
        List<EmployeeDTO> employeesDto = new ArrayList<>();
        employees.forEach(employee -> employeesDto.add(convertEmployeeToEmployeeDTO(employee)));
        return employeesDto;
    }

    private EmployeeDTO convertEmployeeToEmployeeDTO(Employee employee){
        EmployeeDTO employeeDTO = new EmployeeDTO();
        BeanUtils.copyProperties(employee, employeeDTO);
        return employeeDTO;
    }

    private Employee convertEmployeeDTOToEntity(EmployeeDTO employeeDTO) {
        Employee employee = new Employee();
        BeanUtils.copyProperties(employeeDTO, employee);
        return employee;
    }

    private CustomerDTO convertCustomerToCustomerDTO(Customer customer){
        CustomerDTO customerDTO = new CustomerDTO();
        BeanUtils.copyProperties(customer, customerDTO);

        if(customer.getPets() != null) {
            List<Long> petIds = customer.getPets().stream().map(pet -> pet.getId()).collect(Collectors.toList());
            customerDTO.setPetIds(petIds);
        }


        return customerDTO;
    }

    private Customer convertCustomerDTOToEntity(CustomerDTO customerDTO) {
        Customer customer = new Customer();
        BeanUtils.copyProperties(customerDTO, customer);
        return customer;
    }

}
