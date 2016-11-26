package org.tadp.dragonball

import org.tadp.dragonball.Criterios._
import org.tadp.dragonball.Especies._
import org.tadp.dragonball.Items._
import org.tadp.dragonball.Movimientos.usarItem
import org.tadp.dragonball.Movimientos.dejarseFajar
import org.tadp.dragonball.Movimientos.genkidama
import org.tadp.dragonball.Movimientos.Movimiento
import scala.util._

package object dragonBall{
  
  //▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀
  //               Guerrero
  //▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀
  
  case class Guerrero(nombre: String,
                      especie: Especie,
                      habilidades: List[Movimiento],
                      ki: Int = 0,
                      potenciadorGenkidama: Int = 0,
                      kiMaximo: Int,
                      inventario: List[Item],
                      estado: Estado = Normal) {
    
    /* "SETTERS" */
    def ki (delta: Int) : Guerrero = {
      val nuevoki = (ki + delta min kiMaximo) max 0
      val nuevoGuerrero = copy(ki = nuevoki)
      if(nuevoki==0) nuevoGuerrero.morite else nuevoGuerrero}
    def kiTo(value: Int) : Guerrero = ki(-(ki-value))
    def estado(nuevoEstado: Estado) : Guerrero = copy (estado=nuevoEstado)
    def kiMaximo(nuevoMaximo: Int) : Guerrero = copy (kiMaximo=nuevoMaximo)
    def especie(especie: Especie) : Guerrero = copy (especie=especie)
    def aumentarPotenciador : Guerrero = copy(potenciadorGenkidama = potenciadorGenkidama + 1)
    def perderPotenciador : Guerrero = copy(potenciadorGenkidama = 0)
    def inventario(nuevaLista: List[Item]) :Guerrero = copy(inventario = nuevaLista)
    
    def multiplicarKiMaximoEn(valor :Int) :Guerrero = kiMaximo( kiMaximo * valor)
    
    // Crear la lista de inventario con un item modificado, ej: la arma de fuego con una bala menos
    def item(itemViejo: Item, itemNuevo: Item) : Guerrero = copy(inventario = inventario.updated(inventario.indexOf(itemViejo), itemNuevo))
    
    def esDeEspecie(especie : Especie) = {this.especie == especie}
    
    def hacerMovimiento(movimiento : Movimiento, oponente : Guerrero) : Try[(Guerrero,Guerrero)] = {
      Try(
          
          //TODO: FALTA VERIFICAR QUE TENGA EL MOVIMIENTO EN SU LISTA
      estado match {
        // En caso de estar muerto no pasa nada
        // Aca es donde podria tirar error por estar muerto y querer hacer algo
        case Muerto => (this,oponente)
        // Hay movimientos que se puede hacer por mas que esten inconcientes. Ej: comer semillas
        case Inconsciente => movimiento match{
          case usarItem(SemillaDeErmitanio) => movimiento(this.perderPotenciador, oponente)
          case _ => (this, oponente)
        }
        case _ => movimiento match { 
          case `dejarseFajar` => movimiento(this.aumentarPotenciador,oponente)
          case `genkidama` => movimiento(this,oponente) 
          case _ => movimiento(this.perderPotenciador, oponente)
          }
        }
      )
    }
    
  //▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀
  //      PUNTO 1
  //▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀
    
    def movimientoMasEfectivoContra(oponente: Guerrero, criterio: Criterio) = {    
      
      /*
      val resultados = for {
        movimiento <- this.habilidades
        peleadores = hacerMovimiento(movimiento, oponente)
        if peleadores.isSuccess 
          (atacante, oponente) = peleadores.get
          valor = criterio(atacante, oponente)    
      }yield(movimiento, valor)
      */
      val resultados2 = this.habilidades.map(mov =>(mov,this.hacerMovimiento(mov, oponente)))
                          .filter(_._2.isSuccess)
                          .map{case (move,peleadores) => (move,criterio(peleadores.get._1,peleadores.get._2))}
                          //.filter(_._2 > 0)
      //TODO: Cuando se filtran por el criterio sea positivo fallan los test
                              
                              
      resultados2.sortBy(_._2).reverse.headOption.map(_._1)
    }
    
  //▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀
  //      PUNTO 2
  //▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀
    
    def pelearRound(movimiento: Movimiento)(oponente: Guerrero) = {
      val combatientes = this.hacerMovimiento(movimiento, oponente)
      turnoOponente(combatientes.get._1,combatientes.get._2)
    }
    
  //▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀
  //      PUNTO 3
  //▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀
    
    def planDeAtaqueContra(oponente :Guerrero, cantidadRounds : Int)(criterio :Criterio) : Option[List[Movimiento]] = {
    
     val rounds = List.fill(cantidadRounds) {(atc:Guerrero,opo:Guerrero,plan:List[Movimiento]) => 
                                               val movEfectivo = atc.movimientoMasEfectivoContra(opo, criterio).get
                                               val (nuevoAtc,nuevoOpo) = atc.pelearRound(movEfectivo)(opo).get
                                               (nuevoAtc,nuevoOpo,plan:+movEfectivo)
                                               }
     val plan = Try {
       val (_,_,planAtaque) = rounds.foldLeft((this,oponente,List[Movimiento]() )) {
          case ((atc,opt,plan),round) => round(atc,opt,plan)
          }
       planAtaque
       }
     plan.toOption
     }
    
  //▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀
  //      PUNTO 4
  //▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀
    def pelearContra(oponente :Guerrero)(planDeAtaque : List[Movimiento]) : Pelea = {
  
      try{
        val peleadores:Pelea = PeleaEnCurso(this,oponente)
        val estado : Pelea = planDeAtaque.foldLeft(peleadores){
            (peleadores:Pelea,mov:Movimiento) => peleadores.map {
                (atc,opo) => atc.pelearRound(mov)(opo).get
                }
            }
        estado
      } catch {
        /* La lista de movimientos pasada tiene un movimiento no permitido para el atacante, la pelea se cancela */
        case e:InvalidAttackException => PeleaCancelada(this,oponente,e)
      }
    }
  
  //▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀
  //     CAMBIOS DE ESTADO
  //▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀    
    def recuperaKiMaximo :Guerrero = this.kiTo( this.kiMaximo)
    
    
    def morite :Guerrero = {
      val nuevoGuerrero : Guerrero = this.perderPotenciador

      nuevoGuerrero.especie match{
        case fusion @Fusion(_) => fusion.guerroBase.copy(estado = Muerto, ki = 0)
        case _ => nuevoGuerrero.estado(Muerto).copy(ki = 0)
      }
    }
    
    def poneteInconsciente :Guerrero =  {
      val nuevoGuerrero : Guerrero = this.perderPotenciador
      
      this.especie match {
        case Saiyajin(_,_) => nuevoGuerrero.perdeSSJ.estado(Inconsciente)
        case fusion @Fusion(_) => fusion.guerroBase.estado(Inconsciente)
        case _ => nuevoGuerrero.estado(Inconsciente)
      }
      
    }
    def revitalizate :Guerrero = {
      
      estado match{
        case Inconsciente =>
          this.estado(Normal)
        case _ => this
          
      }
    }
    
    def perdeSSJ :Guerrero = {
      
      this.especie match{
        case saiyajin @Saiyajin(cola, Some(_)) =>
          val nivel = saiyajin.nivelSaiyajin
          val kiInicial : Int = this.kiMaximo / Math.pow(5,nivel).asInstanceOf[Int]
      
          this.kiMaximo(kiInicial).especie(Saiyajin(cola, None))
        case _ => this
      }
    }
  } 

  //aca termina el guerrero
  
  def turnoOponente(atacante: Guerrero, oponente:Guerrero):Try[(Guerrero,Guerrero)] = {
    val combatientes = 
      oponente.hacerMovimiento(oponente.movimientoMasEfectivoContra(atacante, diferenciaDeKi).get, atacante)
      combatientes.map {case (oponente,atacante) => (atacante,oponente)}
  }
  
  //▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀
  //  FORMAS DE DIGESTION DE LOS MONSTRUOS
  //▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀
  
  def soloAndroides(monstruo :Guerrero, comida : Guerrero) : (Guerrero, Guerrero) = {
    
    monstruo.especie match {
       case espMonstruo@Monstruo(_,_) =>
         comida.especie match{
           case esp@Androide => (monstruo.especie(espMonstruo.movimientos(espMonstruo.movimientosAprendidosPorDigestion ++ comida.habilidades)),comida.morite)
           case _ => throw new InvalidAttackException("El monstruo no puede comer guerreros no androides")
         }
       case _ => (monstruo,comida)
    }
  }
  
  def comerTodo(monstruo : Guerrero, comida : Guerrero) : (Guerrero, Guerrero) = {
   val monstruoLleno = monstruo.especie match{
      case especie @Monstruo(_,_) => monstruo.especie(especie.movimientos(comida.habilidades))
      case _ => throw new InvalidAttackException("Solo los monstruos pueden comer")
    }
                                                
    (monstruoLleno, comida.morite)
  }
  
  //▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀
  //  MOVIMIENTOS DEL SAIYAJIN
  //▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀

  //retorna si el guerrero(saiyajin) puede avanzar un nivel se SSJ
  def puedeConvertirseEnSJJ(saiyajin :Guerrero): Boolean = saiyajin.ki >= (saiyajin.kiMaximo /2)
  
  //retorna el nivel del saiyajin, si esta en estado normal, es 0
  def nivelDelSaiyajin(saiyajin :Guerrero):Int = saiyajin.especie match{case saiyajin @Saiyajin(_,_) => saiyajin.nivelSaiyajin
                                                                        case _ => throw new RuntimeException("Solo los saiyajines tienen nivel")}
   
  
  //▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀
  //            Estados
  //▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀

  sealed trait Estado
  case object Muerto extends Estado
  case object Inconsciente extends Estado
  case object Normal extends Estado
  type Criterio = (Guerrero,Guerrero) => Int
  
  //▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀
  //          Transformaciones
  //▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀
  
  sealed trait Transformacion
  case class SSJ(nivel: Int) extends Transformacion
  case class Mono(estadoAnterior: Guerrero) extends Transformacion
  
  //▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀
  //        RESULTADOS DE PELEA
  //▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀
 
  sealed abstract class Pelea {
    def map(f : Movimiento): Pelea
    def filter(p: (Guerrero,Guerrero) => Boolean): Pelea
    def flatmap(f: (Guerrero,Guerrero) => Pelea): Pelea
    //TODO: resolver el fold para las PeleaTerminada
    //def fold[T](e : ((Guerrero,Guerrero) => T)) (f:((Guerrero,Guerrero)=>T)): T
  }
  case class PeleaCancelada(atc:Guerrero,opo:Guerrero,motivo:Exception) extends Pelea {
    def map(f: Movimiento):Pelea = this
    def flatmap(f: (Guerrero,Guerrero) => Pelea ):Pelea = this
    def filter(p: (Guerrero,Guerrero) => Boolean): Pelea = this
    //TODO: Fijarse si el fold podria usar una monada como retorno
    //def fold[T](e : ((Guerrero,Guerrero) => T)) (f:((Guerrero,Guerrero)=>T)): T = None
    
    def get : Guerrero = atc
  }
  
  case class PeleaEnCurso(atc:Guerrero,opo:Guerrero) extends Pelea {
    def map(f: Movimiento):Pelea = {
      
      val (nuevoAtc,nuevoOpo) = f(atc,opo)
      
      val estados = (nuevoAtc.estado,nuevoOpo.estado)
      estados match{
        case (_,Muerto) => PeleaTerminada(nuevoAtc)
        case (Muerto,_) => PeleaTerminada(nuevoOpo)
        case (_,_) => PeleaEnCurso(nuevoAtc,nuevoOpo)
      }  
    }
    
    def get : (Guerrero,Guerrero) = (this.atc,this.opo)
    
    def filter(p: (Guerrero,Guerrero) => Boolean): Pelea = {
      if (p(this.atc,this.opo)) this else PeleaCancelada(this.atc,this.opo,InvalidAttackException("ads"))
    }
    
    def flatmap(f: (Guerrero,Guerrero) => Pelea ):Pelea = f(this.atc,this.opo)
    
    def fold[T](e : ((Guerrero,Guerrero) => T)) (f:((Guerrero,Guerrero)=>T)): T = e(this.atc,this.opo)
    
  }
  
  case class PeleaTerminada(ganador:Guerrero) extends Pelea {
    def map(f: Movimiento):Pelea = this
    def flatmap(f: (Guerrero,Guerrero) => Pelea ):Pelea = this
    def filter(p: (Guerrero,Guerrero) => Boolean): Pelea = this
    //TODO: Fijarse si el fold podria usar una monada como retorno
    //def fold[T](e : ((Guerrero,Guerrero) => T)) (f:((Guerrero,Guerrero)=>T)): T = None
    
    def get : Guerrero = ganador
  }
  
  case class InvalidAttackException(s:String) extends Exception(s)
  
}