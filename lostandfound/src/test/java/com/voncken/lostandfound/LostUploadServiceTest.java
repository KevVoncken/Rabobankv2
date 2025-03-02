package com.voncken.lostandfound;

import com.voncken.lostandfound.Repositories.LostItemRepository;
import com.voncken.lostandfound.Repositories.UserClaimRepository;
import com.voncken.lostandfound.Repositories.States.LostItemState;
import com.voncken.lostandfound.Repositories.States.UserClaimState;
import com.voncken.lostandfound.Services.LostItemService;
import com.voncken.lostandfound.Services.UserClaimService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class LostUploadServiceTest {

    @Mock
    private LostItemRepository lostItemRepository;

    @Mock
	private UserClaimRepository userClaimRepository;

    private LostItemService lostItemService;
	private UserClaimService userClaimService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        lostItemService = new LostItemService(lostItemRepository);
		userClaimService = new UserClaimService(userClaimRepository);
    }

    @Test
    void getAllLostItemReturnsRepositoryItems() {
            List<LostItemState> expectedItems = Arrays.asList(
            new LostItemState(), 
            new LostItemState()
        );
        when(lostItemRepository.GetAll()).thenReturn(expectedItems);

        Iterable<LostItemState> result = lostItemService.GetAll();

        assertEquals(expectedItems, result);
        verify(lostItemRepository, times(1)).GetAll();
    }

	@Test
    void getAllUserClaimReturnsRepositoryItems() {

        List<UserClaimState> expectedItems = Arrays.asList(
            new UserClaimState(), 
            new UserClaimState()
        );
        when(userClaimRepository.GetAll()).thenReturn(expectedItems);

        Iterable<UserClaimState> result = userClaimService.GetAll();

        assertEquals(expectedItems, result);
        verify(userClaimRepository, times(1)).GetAll();
    }
}