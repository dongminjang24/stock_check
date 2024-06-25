package com.example.stock.service;


import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.example.stock.domain.Stock;
import com.example.stock.facade.LettuceLockStockFacade;
import com.example.stock.facade.NamedLockStockFacade;
import com.example.stock.facade.OptimisticLockStockFacade;
import com.example.stock.facade.RedissonLockStockFacade;
import com.example.stock.repository.StockRepository;

@SpringBootTest
class StockServiceTest {

	@Autowired
	private RedissonLockStockFacade redissonLockStockFacade;

	@Autowired
	private LettuceLockStockFacade lettuceLockStockFacade;

	@Autowired
	private StockService stockService;

	@Autowired
	private PessimisticLockStockService pessimisticLockStockService;

	@Autowired
	private OptimisticLockStockFacade optimisticLockStockFacade;

	@Autowired
	private NamedLockStockFacade namedLockStockFacade;

	@Autowired
	private StockRepository stockRepository;

	@BeforeEach
	public void before() {
		stockRepository.saveAndFlush(new Stock(1L, 100L));
	}

	@AfterEach
	public void after() {
		stockRepository.deleteAll();
	}

	@Test
	public void 재고감소() {
		stockService.decrease(1L, 1L);

		Stock stock = stockRepository.findById(1L).orElseThrow();

		Assertions.assertEquals(99, stock.getQuantity());
	}

	@Test
	public void 동시에_100개의_요청() throws InterruptedException {
		int threadCount = 100;
		// 비동기로 실행하는 작업을 단순화하여 사용할 수 있게 도와주는 java api
		ExecutorService executorService = Executors.newFixedThreadPool(32);

		//다른 쓰레드에서 수행중인 작업이 완료될때까지 대기할수 있게 도와주는 클래스
		CountDownLatch countDownLatch = new CountDownLatch(threadCount);


			for (int i = 0; i < threadCount; i++) {
				executorService.submit(() -> {
					try {
						stockService.decrease(1L, 1L);
					} finally {
						countDownLatch.countDown();
					}
				});

			}

		countDownLatch.await();

		Stock stock = stockRepository.findById(1L).orElseThrow();
		Assertions.assertEquals(0L, stock.getQuantity());


	}


	@Test
	public void 동시에_100개의_요청_pessimistic() throws InterruptedException {
		int threadCount = 100;
		// 비동기로 실행하는 작업을 단순화하여 사용할 수 있게 도와주는 java api
		ExecutorService executorService = Executors.newFixedThreadPool(32);

		//다른 쓰레드에서 수행중인 작업이 완료될때까지 대기할수 있게 도와주는 클래스
		CountDownLatch countDownLatch = new CountDownLatch(threadCount);


		for (int i = 0; i < threadCount; i++) {
			executorService.submit(() -> {
				try {
					pessimisticLockStockService.decrease(1L, 1L);
				} finally {
					countDownLatch.countDown();
				}
			});

		}

		countDownLatch.await();

		Stock stock = stockRepository.findById(1L).orElseThrow();
		Assertions.assertEquals(0L, stock.getQuantity());


	}


	@Test
	public void 동시에_100개의_요청_optimistic() throws InterruptedException {
		int threadCount = 100;
		// 비동기로 실행하는 작업을 단순화하여 사용할 수 있게 도와주는 java api
		ExecutorService executorService = Executors.newFixedThreadPool(32);

		//다른 쓰레드에서 수행중인 작업이 완료될때까지 대기할수 있게 도와주는 클래스
		CountDownLatch countDownLatch = new CountDownLatch(threadCount);


		for (int i = 0; i < threadCount; i++) {
			executorService.submit(() -> {
				try {
					optimisticLockStockFacade.decrease(1L, 1L);
				} catch (InterruptedException e) {
					throw new RuntimeException(e);
				} finally {
					countDownLatch.countDown();
				}
			});

		}

		countDownLatch.await();

		Stock stock = stockRepository.findById(1L).orElseThrow();
		Assertions.assertEquals(0L, stock.getQuantity());


	}


	@Test
	public void 동시에_100개의_요청_named() throws InterruptedException {
		int threadCount = 100;
		// 비동기로 실행하는 작업을 단순화하여 사용할 수 있게 도와주는 java api
		ExecutorService executorService = Executors.newFixedThreadPool(32);

		//다른 쓰레드에서 수행중인 작업이 완료될때까지 대기할수 있게 도와주는 클래스
		CountDownLatch countDownLatch = new CountDownLatch(threadCount);


		for (int i = 0; i < threadCount; i++) {
			executorService.submit(() -> {
				try {
					namedLockStockFacade.decrease(1L, 1L);
				} finally {
					countDownLatch.countDown();
				}
			});

		}

		countDownLatch.await();

		Stock stock = stockRepository.findById(1L).orElseThrow();
		Assertions.assertEquals(0L, stock.getQuantity());


	}


	@Test
	public void 동시에_100개의_요청_lettuce() throws InterruptedException {
		int threadCount = 100;
		// 비동기로 실행하는 작업을 단순화하여 사용할 수 있게 도와주는 java api
		ExecutorService executorService = Executors.newFixedThreadPool(32);

		//다른 쓰레드에서 수행중인 작업이 완료될때까지 대기할수 있게 도와주는 클래스
		CountDownLatch countDownLatch = new CountDownLatch(threadCount);


		for (int i = 0; i < threadCount; i++) {
			executorService.submit(() -> {
				try {
					lettuceLockStockFacade.decrease(1L, 1L);
				} catch (InterruptedException e) {
					throw new RuntimeException(e);
				} finally {
					countDownLatch.countDown();
				}
			});

		}

		countDownLatch.await();

		Stock stock = stockRepository.findById(1L).orElseThrow();
		Assertions.assertEquals(0L, stock.getQuantity());


	}


	@Test
	public void 동시에_100개의_요청_redisson() throws InterruptedException {
		int threadCount = 100;
		// 비동기로 실행하는 작업을 단순화하여 사용할 수 있게 도와주는 java api
		ExecutorService executorService = Executors.newFixedThreadPool(32);

		//다른 쓰레드에서 수행중인 작업이 완료될때까지 대기할수 있게 도와주는 클래스
		CountDownLatch countDownLatch = new CountDownLatch(threadCount);


		for (int i = 0; i < threadCount; i++) {
			executorService.submit(() -> {
				try {
					redissonLockStockFacade.decrease(1L, 1L);
				} finally {
					countDownLatch.countDown();
				}
			});

		}

		countDownLatch.await();

		Stock stock = stockRepository.findById(1L).orElseThrow();
		Assertions.assertEquals(0L, stock.getQuantity());


	}

}