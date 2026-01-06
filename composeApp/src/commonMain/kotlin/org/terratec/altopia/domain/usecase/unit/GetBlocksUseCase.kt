package org.terratec.altopia.domain.usecase.unit

import org.terratec.altopia.domain.model.Block
import org.terratec.altopia.domain.repository.UnitRepository

class GetBlocksUseCase(
    private val repository: UnitRepository
) {
    suspend operator fun invoke(): Result<List<Block>> {
        return repository.getBlocks()
    }
}
