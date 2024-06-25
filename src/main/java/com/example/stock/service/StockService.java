package com.example.stock.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.example.stock.domain.Stock;
import com.example.stock.repository.StockRepository;

@Service
public class StockService {
	private final StockRepository stockRepository;

	public StockService(StockRepository stockRepository) {
		this.stockRepository = stockRepository;
	}


	// @Transactional
	// synchronized는 여러서버에서 작동하게 된다면 결국에는 race condition이 발생하게 됨
	// public synchronized void decrease(Long id, Long quantity) {
	//NamedLock 할때 부모 트랜잭션과 별도로 실행이 되어야 하기 때문에 propagation requires new를 걸어줌.
	// @Transactional(propagation = Propagation.REQUIRES_NEW)
	public void decrease(Long id, Long quantity) {
		// Stock 조회
		// 재고를 감소신뒤
		// 갱신된 값을 저장하도록 하겠습니다.
		Stock stock = stockRepository.findById(id) .orElseThrow();
		stock.decrease(quantity);

		stockRepository.saveAndFlush(stock);
	}
}
